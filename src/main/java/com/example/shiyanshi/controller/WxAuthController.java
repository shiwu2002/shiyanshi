package com.example.shiyanshi.controller;

import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.User;
import com.example.shiyanshi.entity.UserWechatAuth;
import com.example.shiyanshi.service.UserService;
import com.example.shiyanshi.service.UserWechatAuthService;
import com.example.shiyanshi.util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序登录控制器
 *
 * 功能：
 * - 通过前端传入的 wx.login(code) 获取的 code，调用微信 jscode2session 接口换取 openid / session_key / unionid
 * - 若系统已存在绑定关系，则签发系统JWT返回；否则返回微信侧身份信息并标记 needBind=true
 * - 提供绑定接口，将 openid/unionid 绑定到指定 userId
 *
 * 配置项（application.properties）：
 * wx.appid=你的appid
 * wx.secret=你的secret
 *
 * 微信接口文档：
 * https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html
 */
@RestController
@RequestMapping("/api/wx")
@CrossOrigin
public class WxAuthController {

    @Value("${wx.appid:}")
    private String appId;

    @Value("${wx.secret:}")
    private String appSecret;

    private static final String PLATFORM_MINI_PROGRAM = "mini_program";

    private static final String JSCODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    @Autowired
    private UserWechatAuthService userWechatAuthService;

    @Autowired
    private UserService userService;

    /**
     * 通过 code 登录（换取 openid / session_key）
     *
     * 请求示例：
     * POST /api/wx/login
     * {
     *   "code": "wx.login返回的code"
     * }
     *
     * 响应：
     * - 若已绑定用户：返回系统JWT与用户信息，needBind=false
     * - 若未绑定：返回 openid/sessionKey/unionid，needBind=true
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> loginByCode(@RequestBody Map<String, String> body) {
        try {
            String code = body.get("code");
            if (code == null || code.trim().isEmpty()) {
                return Result.error("缺少必填参数：code");
            }

            if (appId == null || appId.isEmpty() || appSecret == null || appSecret.isEmpty()) {
                return Result.error("微信小程序AppId或Secret未配置，请在application.properties中设置 wx.appid 与 wx.secret");
            }

            String url = String.format(JSCODE2SESSION_URL, appId, appSecret, code);
            RestTemplate client = new RestTemplate();
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = client.getForObject(url, Map.class);

            if (resp == null) {
                return Result.error("微信接口无响应");
            }

            // 微信错误返回格式示例：{"errcode":40029,"errmsg":"invalid code"}
            if (resp.containsKey("errcode")) {
                Object errcode = resp.get("errcode");
                Object errmsg = resp.get("errmsg");
                return Result.error(String.format("微信登录失败(errcode=%s): %s", String.valueOf(errcode), String.valueOf(errmsg)));
            }

            String openid = (String) resp.get("openid");
            String sessionKey = (String) resp.get("session_key");
            String unionid = (String) resp.get("unionid"); // 可能为空

            if (openid == null || sessionKey == null) {
                return Result.error("微信返回数据不完整：缺少openid或session_key");
            }

            // 查询是否已绑定系统用户
            UserWechatAuth auth = userWechatAuthService.findByPlatformAndOpenid(PLATFORM_MINI_PROGRAM, openid);
            Map<String, Object> data = new HashMap<>();

            if (auth != null && auth.getBindStatus() != null && auth.getBindStatus() == 1 && auth.getUserId() != null) {
                // 已绑定 → 返回系统JWT与用户信息
                User user = userService.findById(auth.getUserId());
                if (user != null) {
                    String token = JWTUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
                    userWechatAuthService.touchLogin(PLATFORM_MINI_PROGRAM, openid, sessionKey);

                    data.put("needBind", false);
                    data.put("token", token);
                    data.put("userId", user.getId());
                    data.put("username", user.getUsername());
                    data.put("userType", user.getUserType());
                    data.put("realName", user.getRealName());
                    data.put("openid", openid);
                    data.put("unionid", unionid);
                    return Result.success("微信登录成功（已绑定）", data);
                }
            }

            // 未绑定 → 返回微信侧身份信息
            data.put("openid", openid);
            data.put("sessionKey", sessionKey);
            data.put("unionid", unionid);
            data.put("needBind", true);
            return Result.success("微信登录成功（未绑定）", data);
        } catch (Exception e) {
            return Result.error("微信登录异常：" + e.getMessage());
        }
    }

    /**
     * 绑定 openid 到指定用户
     *
     * 请求示例：
     * POST /api/wx/bind
     * {
     *   "userId": 1,
     *   "openid": "...",
     *   "unionid": null,
     *   "sessionKey": "...",
     *   "platform": "mini_program" // 可选，默认 mini_program
     * }
     *
     * 响应：绑定成功后返回 minimal 信息；若需要可直接返回JWT
     *
     * 注意：生产环境建议校验当前登录态与 userId 的一致性（仅本人可绑定）
     */
    @PostMapping("/bind")
    public Result<Map<String, Object>> bindOpenid(@RequestBody Map<String, Object> body) {
        try {
            Long userId = body.get("userId") == null ? null : Long.valueOf(body.get("userId").toString());
            String openid = body.get("openid") == null ? null : body.get("openid").toString();
            String unionid = body.get("unionid") == null ? null : body.get("unionid").toString();
            String sessionKey = body.get("sessionKey") == null ? null : body.get("sessionKey").toString();
            String platform = body.get("platform") == null ? PLATFORM_MINI_PROGRAM : body.get("platform").toString();

            if (userId == null || openid == null || openid.trim().isEmpty()) {
                return Result.error("缺少必填参数：userId 或 openid");
            }

            User user = userService.findById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 执行绑定（存在则更新）
            UserWechatAuth record = userWechatAuthService.bind(userId, platform, openid, unionid, sessionKey);

            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("platform", platform);
            data.put("openid", record.getOpenid());
            data.put("unionid", record.getUnionid());
            data.put("bindStatus", record.getBindStatus());

            // 可选：绑定后直接返回系统JWT，提升体验
            String token = JWTUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
            data.put("token", token);

            return Result.success("绑定成功", data);
        } catch (Exception e) {
            return Result.error("绑定失败：" + e.getMessage());
        }
    }
}
