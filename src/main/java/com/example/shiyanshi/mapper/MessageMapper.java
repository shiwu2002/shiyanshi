package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiyanshi.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 消息数据访问层
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 根据接收者ID查询消息列表
     */
    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} AND deleted = 0 ORDER BY create_time DESC")
    List<Message> findByReceiverId(@Param("receiverId") Long receiverId);
    
    /**
     * 根据接收者ID和消息类型查询消息列表
     */
    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} AND message_type = #{messageType} AND deleted = 0 ORDER BY create_time DESC")
    List<Message> findByReceiverIdAndType(@Param("receiverId") Long receiverId, @Param("messageType") String messageType);
    
    /**
     * 根据接收者ID查询未读消息列表
     */
    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} AND is_read = 0 AND deleted = 0 ORDER BY create_time DESC")
    List<Message> findUnreadByReceiverId(@Param("receiverId") Long receiverId);
    
    /**
     * 统计接收者的未读消息数量
     */
    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{receiverId} AND is_read = 0 AND deleted = 0")
    int countUnreadByReceiverId(@Param("receiverId") Long receiverId);
    
    /**
     * 根据消息类型统计接收者的未读消息数量
     */
    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{receiverId} AND message_type = #{messageType} AND is_read = 0 AND deleted = 0")
    int countUnreadByReceiverIdAndType(@Param("receiverId") Long receiverId, @Param("messageType") String messageType);
    
    /**
     * 标记消息为已读
     */
    @Update("UPDATE message SET is_read = 1, read_time = NOW() WHERE id = #{id}")
    int markAsRead(@Param("id") Long id);
    
    /**
     * 批量标记消息为已读
     */
    @Update("<script>" +
            "UPDATE message SET is_read = 1, read_time = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchMarkAsRead(@Param("ids") List<Long> ids);
    
    /**
     * 标记接收者的所有消息为已读
     */
    @Update("UPDATE message SET is_read = 1, read_time = NOW() WHERE receiver_id = #{receiverId} AND is_read = 0 AND deleted = 0")
    int markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);
    
    /**
     * 软删除消息
     */
    @Update("UPDATE message SET deleted = 1 WHERE id = #{id}")
    int softDelete(@Param("id") Long id);
    
    /**
     * 批量软删除消息
     */
    @Update("<script>" +
            "UPDATE message SET deleted = 1 WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchSoftDelete(@Param("ids") List<Long> ids);
    
    /**
     * 根据发送者ID查询消息列表
     */
    @Select("SELECT * FROM message WHERE sender_id = #{senderId} AND deleted = 0 ORDER BY create_time DESC")
    List<Message> findBySenderId(@Param("senderId") Long senderId);
    
    /**
     * 根据相关业务查询消息列表
     */
    @Select("SELECT * FROM message WHERE related_id = #{relatedId} AND related_type = #{relatedType} AND deleted = 0 ORDER BY create_time DESC")
    List<Message> findByRelated(@Param("relatedId") Long relatedId, @Param("relatedType") String relatedType);
    
    /**
     * 分页查询接收者的消息列表
     */
    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Message> findByReceiverIdWithPage(@Param("receiverId") Long receiverId, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据优先级查询消息列表
     */
    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} AND priority = #{priority} AND deleted = 0 ORDER BY create_time DESC")
    List<Message> findByReceiverIdAndPriority(@Param("receiverId") Long receiverId, @Param("priority") Integer priority);
}
