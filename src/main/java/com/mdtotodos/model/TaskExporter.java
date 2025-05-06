package com.mdtotodos.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 任务导出器，用于将任务导出到不同的目标平台
 */
public class TaskExporter {
    
    /**
     * 导出平台类型枚举
     */
    public enum ExportPlatform {
        CSV,
        JSON,
        APPLE_REMINDERS,
        MICROSOFT_TODO,
        GOOGLE_TASKS
    }
    
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 将任务导出到CSV文件
     * 
     * @param tasks 任务列表
     * @param file 目标文件
     * @throws IOException 如果写入文件出错
     */
    public void exportToCSV(List<Task> tasks, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // 写入CSV头
            writer.write("title,description,due_date\n");
            
            // 写入每个任务
            for (Task task : tasks) {
                StringBuilder sb = new StringBuilder();
                
                // 标题（处理逗号）
                sb.append("\"").append(task.getTitle().replace("\"", "\"\"")).append("\"").append(",");
                
                // 描述（处理逗号）
                sb.append("\"").append(task.getDescription().replace("\"", "\"\"")).append("\"").append(",");
                
                // 截止日期
                if (task.hasDueDate()) {
                    sb.append("\"").append(task.getDueDate().format(DATE_FORMATTER)).append("\"");
                } else {
                    sb.append("\"\"");
                }
                
                sb.append("\n");
                writer.write(sb.toString());
            }
        }
    }
    
    /**
     * 将任务导出到JSON文件
     * 
     * @param tasks 任务列表
     * @param file 目标文件
     * @throws IOException 如果写入文件出错
     */
    public void exportToJSON(List<Task> tasks, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[\n");
            
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                writer.write("  {\n");
                
                // 标题
                writer.write("    \"title\": \"" + escapeJson(task.getTitle()) + "\",\n");
                
                // 描述
                writer.write("    \"description\": \"" + escapeJson(task.getDescription()) + "\"");
                
                // 截止日期
                if (task.hasDueDate()) {
                    writer.write(",\n    \"due_date\": \"" + 
                            task.getDueDate().format(DATE_FORMATTER) + "\"");
                }
                
                writer.write("\n  }");
                
                // 如果不是最后一个任务，添加逗号
                if (i < tasks.size() - 1) {
                    writer.write(",");
                }
                
                writer.write("\n");
            }
            
            writer.write("]\n");
        }
    }
    
    /**
     * 将任务导出到Apple提醒事项
     * 注意：该功能仅在macOS上可用，且需要额外的JNI实现
     * 
     * @param tasks 任务列表
     * @throws UnsupportedOperationException 如果运行在非macOS系统上
     * @throws IOException 如果导出过程出错
     */
    public void exportToAppleReminders(List<Task> tasks) throws UnsupportedOperationException, IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("mac")) {
            throw new UnsupportedOperationException("Apple提醒事项功能仅在macOS上可用");
        }
        
        // TODO: 实现macOS上的JNI调用，这需要单独的原生库
        throw new UnsupportedOperationException("Apple提醒事项功能尚未实现");
    }
    
    /**
     * 将任务导出到Microsoft To Do
     * 注意：该功能需要Microsoft Graph API认证
     * 
     * @param tasks 任务列表
     * @throws UnsupportedOperationException 该功能尚未实现
     */
    public void exportToMicrosoftToDo(List<Task> tasks) throws UnsupportedOperationException {
        // TODO: 实现Microsoft Graph API调用
        throw new UnsupportedOperationException("Microsoft To Do功能尚未实现");
    }
    
    /**
     * 将任务导出到Google Tasks
     * 注意：该功能需要Google API认证
     * 
     * @param tasks 任务列表
     * @throws UnsupportedOperationException 该功能尚未实现
     */
    public void exportToGoogleTasks(List<Task> tasks) throws UnsupportedOperationException {
        // TODO: 实现Google Tasks API调用
        throw new UnsupportedOperationException("Google Tasks功能尚未实现");
    }
    
    /**
     * 转义JSON字符串
     * 
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
} 