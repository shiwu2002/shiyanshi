package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.entity.Reservation;
import com.example.shiyanshi.service.ReservationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报表导出Controller
 * 提供预约数据的Excel导出功能
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReservationService reservationService;

    /**
     * 导出预约报表为Excel文件
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param laboratoryId 实验室ID（可选）
     * @param status 预约状态（可选）
     * @param response HTTP响应对象
     */
    @RequirePermission(value = 2, description = "导出预约报表需要管理员及以上权限")
    @GetMapping("/export-reservations")
    public void exportReservations(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Long laboratoryId,
            @RequestParam(required = false) Integer status,
            HttpServletResponse response) throws IOException {

        // 1. 查询数据
        List<Reservation> reservations = reservationService.search(null, status, startDate, endDate);
        
        // 如果指定了实验室ID，需要手动过滤
        if (laboratoryId != null) {
            reservations = reservations.stream()
                    .filter(r -> laboratoryId.equals(r.getLabId()))
                    .toList();
        }

        // 3. 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("预约报表");

        // 4. 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // 5. 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "预约ID", "用户姓名", "实验室名称", "预约日期", "时间段", 
            "使用人数", "实验名称", "使用目的", "使用设备", "状态", 
            "审核人", "审核意见", "提交时间", "审核时间"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 6. 填充数据
        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (Reservation reservation : reservations) {
            Row row = sheet.createRow(rowNum++);
            
            // 预约ID
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(reservation.getId());
            cell0.setCellStyle(dataStyle);
            
            // 用户姓名
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(reservation.getUserName() != null ? reservation.getUserName() : "");
            cell1.setCellStyle(dataStyle);
            
            // 实验室名称
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(reservation.getLabName() != null ? reservation.getLabName() : "");
            cell2.setCellStyle(dataStyle);
            
            // 预约日期
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(reservation.getReserveDate() != null ? 
                reservation.getReserveDate().format(dateFormatter) : "");
            cell3.setCellStyle(dateStyle);
            
            // 时间段
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(reservation.getTimeSlot() != null ? reservation.getTimeSlot() : "");
            cell4.setCellStyle(dataStyle);
            
            // 使用人数
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(reservation.getPeopleNum() != null ? reservation.getPeopleNum() : 0);
            cell5.setCellStyle(dataStyle);
            
            // 实验名称
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(reservation.getExperimentName() != null ? reservation.getExperimentName() : "");
            cell6.setCellStyle(dataStyle);
            
            // 使用目的
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(reservation.getPurpose() != null ? reservation.getPurpose() : "");
            cell7.setCellStyle(dataStyle);
            
            // 使用设备
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(reservation.getEquipment() != null ? reservation.getEquipment() : "");
            cell8.setCellStyle(dataStyle);
            
            // 状态
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(getStatusText(reservation.getStatus()));
            cell9.setCellStyle(dataStyle);
            
            // 审核人
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(reservation.getApprover() != null ? reservation.getApprover() : "");
            cell10.setCellStyle(dataStyle);
            
            // 审核意见
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(reservation.getApproveComment() != null ? reservation.getApproveComment() : "");
            cell11.setCellStyle(dataStyle);
            
            // 提交时间
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(reservation.getCreateTime() != null ? 
                reservation.getCreateTime().format(dateTimeFormatter) : "");
            cell12.setCellStyle(dateStyle);
            
            // 审核时间
            Cell cell13 = row.createCell(13);
            cell13.setCellValue(reservation.getApproveTime() != null ? 
                reservation.getApproveTime().format(dateTimeFormatter) : "");
            cell13.setCellStyle(dateStyle);
        }

        // 7. 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // 设置最小宽度，防止中文显示不全
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 3000);
            }
            // 设置最大宽度，防止过宽
            if (sheet.getColumnWidth(i) > 10000) {
                sheet.setColumnWidth(i, 10000);
            }
        }

        // 8. 设置响应头，让浏览器下载文件
        String fileName = generateFileName(startDate, endDate);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", 
            "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        // 9. 将工作簿写入响应输出流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            outputStream.flush();
        } finally {
            workbook.close();
        }
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置背景色（浅蓝色）
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置字体
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    /**
     * 创建数据样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // 设置对齐方式
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // 设置自动换行
        style.setWrapText(true);
        
        return style;
    }

    /**
     * 创建日期样式
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * 将状态代码转换为中文文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "已通过";
            case 2:
                return "已拒绝";
            case 3:
                return "已取消";
            case 4:
                return "已完成";
            default:
                return "未知";
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName(LocalDate startDate, LocalDate endDate) {
        StringBuilder fileName = new StringBuilder("预约报表");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        
        if (startDate != null && endDate != null) {
            fileName.append("_")
                    .append(startDate.format(formatter))
                    .append("-")
                    .append(endDate.format(formatter));
        } else if (startDate != null) {
            fileName.append("_从")
                    .append(startDate.format(formatter));
        } else if (endDate != null) {
            fileName.append("_至")
                    .append(endDate.format(formatter));
        } else {
            fileName.append("_")
                    .append(LocalDate.now().format(formatter));
        }
        
        fileName.append(".xlsx");
        return fileName.toString();
    }

    /**
     * 导出统计报表
     * 包含预约数量、使用率等统计信息
     */
    @RequirePermission(value = 2, description = "导出统计报表需要管理员及以上权限")
    @GetMapping("/export-statistics")
    public void exportStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletResponse response) throws IOException {

        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("预约统计");

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // 创建标题
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("实验室预约系统统计报表");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

        // 统计周期
        Row periodRow = sheet.createRow(1);
        Cell periodCell = periodRow.createCell(0);
        String period = "统计周期: ";
        if (startDate != null && endDate != null) {
            period += startDate + " 至 " + endDate;
        } else {
            period += "全部";
        }
        periodCell.setCellValue(period);

        // 获取统计数据
        List<Reservation> allReservations = reservationService.search(null, null, startDate, endDate);
        
        // 统计各状态数量
        long totalCount = allReservations.size();
        long pendingCount = allReservations.stream().filter(r -> Integer.valueOf(0).equals(r.getStatus())).count();
        long approvedCount = allReservations.stream().filter(r -> Integer.valueOf(1).equals(r.getStatus())).count();
        long rejectedCount = allReservations.stream().filter(r -> Integer.valueOf(2).equals(r.getStatus())).count();
        long cancelledCount = allReservations.stream().filter(r -> Integer.valueOf(3).equals(r.getStatus())).count();
        long completedCount = allReservations.stream().filter(r -> Integer.valueOf(4).equals(r.getStatus())).count();

        // 创建统计表格
        int rowNum = 3;
        
        // 表头
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("统计项");
        headerRow.createCell(1).setCellValue("数量");
        headerRow.createCell(2).setCellValue("占比");
        for (int i = 0; i < 3; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        // 数据行
        addStatRow(sheet, rowNum++, "总预约数", totalCount, 100.0, dataStyle);
        addStatRow(sheet, rowNum++, "待审核", pendingCount, totalCount > 0 ? pendingCount * 100.0 / totalCount : 0, dataStyle);
        addStatRow(sheet, rowNum++, "已通过", approvedCount, totalCount > 0 ? approvedCount * 100.0 / totalCount : 0, dataStyle);
        addStatRow(sheet, rowNum++, "已拒绝", rejectedCount, totalCount > 0 ? rejectedCount * 100.0 / totalCount : 0, dataStyle);
        addStatRow(sheet, rowNum++, "已取消", cancelledCount, totalCount > 0 ? cancelledCount * 100.0 / totalCount : 0, dataStyle);
        addStatRow(sheet, rowNum++, "已完成", completedCount, totalCount > 0 ? completedCount * 100.0 / totalCount : 0, dataStyle);

        // 调整列宽
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 4000) {
                sheet.setColumnWidth(i, 4000);
            }
        }

        // 设置响应头
        String fileName = "预约统计报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", 
            "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        // 写入响应输出流
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            outputStream.flush();
        } finally {
            workbook.close();
        }
    }

    /**
     * 添加统计行
     */
    private void addStatRow(Sheet sheet, int rowNum, String label, long count, double percentage, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(label);
        cell0.setCellStyle(style);
        
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(count);
        cell1.setCellStyle(style);
        
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(String.format("%.2f%%", percentage));
        cell2.setCellStyle(style);
    }
}
