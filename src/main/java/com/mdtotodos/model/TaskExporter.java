package com.mdtotodos.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 任务导出器，用于将任务导出到ICS日历文件
 */
public class TaskExporter {
    
    /**
     * 导出平台类型枚举
     */
    public enum ExportPlatform {
        ICS
    }
    
    /**
     * 将任务导出到ICS文件（iCalendar格式）
     * 
     * @param tasks 任务列表
     * @param file 目标文件
     * @throws IOException 如果写入文件出错
     */
    public void exportToICS(List<Task> tasks, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // ICS文件头
            writer.write("BEGIN:VCALENDAR\r\n");
            writer.write("VERSION:2.0\r\n");
            writer.write("PRODID:-//MDTOTODOS//Markdown Task Exporter//EN\r\n");
            writer.write("CALSCALE:GREGORIAN\r\n");
            writer.write("METHOD:PUBLISH\r\n");
            
            // 为每个任务创建日历项
            for (Task task : tasks) {
                // 生成唯一标识符
                String uid = UUID.randomUUID().toString();
                
                // 获取当前时间的UTC表示
                String now = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                        .format(java.time.ZonedDateTime.now(ZoneId.of("UTC")));
                
                if (task.hasDueDate()) {
                    // 有截止日期的任务创建为VEVENT（事件）
                    writer.write("BEGIN:VEVENT\r\n");
                    writer.write("UID:" + uid + "\r\n");
                    writer.write("DTSTAMP:" + now + "\r\n");
                    writer.write("CREATED:" + now + "\r\n");
                    writer.write("SUMMARY:" + escapeIcsText(task.getTitle()) + "\r\n");
                    
                    // 如果有描述，则添加
                    if (task.hasDescription()) {
                        writer.write("DESCRIPTION:" + escapeIcsText(task.getDescription()) + "\r\n");
                    }
                    
                    // 计算事件的开始时间和结束时间
                    // 开始时间设为截止日期
                    String dueDateUTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                            .format(task.getDueDate().atZone(ZoneId.systemDefault())
                                    .withZoneSameInstant(ZoneId.of("UTC")));
                    writer.write("DTSTART:" + dueDateUTC + "\r\n");
                    
                    // 结束时间设为开始时间后1小时
                    String endDateUTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                            .format(task.getDueDate().plusHours(1).atZone(ZoneId.systemDefault())
                                    .withZoneSameInstant(ZoneId.of("UTC")));
                    writer.write("DTEND:" + endDateUTC + "\r\n");
                    
                    // 设置提醒（比如在15分钟前）
                    writer.write("BEGIN:VALARM\r\n");
                    writer.write("ACTION:DISPLAY\r\n");
                    writer.write("DESCRIPTION:提醒: " + escapeIcsText(task.getTitle()) + "\r\n");
                    writer.write("TRIGGER:-PT15M\r\n");
                    writer.write("END:VALARM\r\n");
                    
                    writer.write("END:VEVENT\r\n");
                } else {
                    // 无截止日期的任务创建为VTODO（待办事项）
                    writer.write("BEGIN:VTODO\r\n");
                    writer.write("UID:" + uid + "\r\n");
                    writer.write("DTSTAMP:" + now + "\r\n");
                    writer.write("CREATED:" + now + "\r\n");
                    writer.write("SUMMARY:" + escapeIcsText(task.getTitle()) + "\r\n");
                    
                    // 如果有描述，则添加
                    if (task.hasDescription()) {
                        writer.write("DESCRIPTION:" + escapeIcsText(task.getDescription()) + "\r\n");
                    }
                    
                    // 设置未完成状态
                    writer.write("STATUS:NEEDS-ACTION\r\n");
                    writer.write("PRIORITY:0\r\n");
                    writer.write("SEQUENCE:0\r\n");
                    
                    writer.write("END:VTODO\r\n");
                }
            }
            
            // ICS文件尾
            writer.write("END:VCALENDAR\r\n");
        }
    }
    
    /**
     * 转义ICS文本
     * 根据RFC 5545规范处理特殊字符
     * 
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    private String escapeIcsText(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\n", "\\n")
                .replace("\r", "")  // 移除CR，避免与ICS格式冲突
                .replace(":", "\\:");  // 转义冒号，这是iCalendar格式中的特殊字符
    }
} 