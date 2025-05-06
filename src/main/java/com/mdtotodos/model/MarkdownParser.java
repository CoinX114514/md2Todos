package com.mdtotodos.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown解析器，用于从Markdown文件中提取待办事项
 */
public class MarkdownParser {
    // 任务基本格式的正则表达式: "数字. 任务内容"
    private static final Pattern TASK_PATTERN = Pattern.compile("^\\s*(\\d+)\\.\\s+(.+)$");
    
    // 日期格式的正则表达式: YYYY/MM/DD-HHam/pm
    private static final Pattern DATE_PATTERN = 
            Pattern.compile("(\\d{4}/\\d{1,2}/\\d{1,2}-\\d{1,2}(?:am|pm))");
    
    // 描述部分的正则表达式: "// 描述内容"
    private static final Pattern DESCRIPTION_PATTERN = 
            Pattern.compile("\\s*//\\s*(.*?)\\s*(?:" + DATE_PATTERN.pattern() + ")?$");

    /**
     * 从Markdown文件中解析待办事项
     * 
     * @param file Markdown文件
     * @return 解析出的任务列表
     * @throws IOException 如果读取文件出错
     */
    public List<Task> parseTasks(File file) throws IOException {
        List<Task> tasks = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过空行或标题行
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                
                Task task = parseTaskLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }
        
        return tasks;
    }

    /**
     * 解析单行任务文本
     * 
     * @param line 任务文本行
     * @return 解析出的任务对象，如果不是任务则返回null
     */
    private Task parseTaskLine(String line) {
        Matcher taskMatcher = TASK_PATTERN.matcher(line);
        if (!taskMatcher.find()) {
            return null;
        }
        
        // 提取任务内容
        String fullContent = taskMatcher.group(2);
        String title = fullContent;
        String description = "";
        LocalDateTime dueDate = null;
        
        // 查找日期
        Matcher dateMatcher = DATE_PATTERN.matcher(fullContent);
        if (dateMatcher.find()) {
            String dateStr = dateMatcher.group(0);
            dueDate = parseDate(dateStr);
            
            // 从内容中移除日期部分（用于后续处理）
            fullContent = fullContent.replace(dateStr, "");
        }
        
        // 查找描述部分（// 后面的内容）
        Matcher descMatcher = DESCRIPTION_PATTERN.matcher(fullContent);
        if (descMatcher.find()) {
            // 分离标题和描述
            int titleEnd = fullContent.indexOf("//");
            title = fullContent.substring(0, titleEnd).trim();
            description = descMatcher.group(1) != null ? descMatcher.group(1).trim() : "";
        }
        
        return new Task(title, description, dueDate);
    }

    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串，格式: YYYY/MM/DD-HHam/pm
     * @return 解析出的日期时间，如果解析失败则返回null
     */
    private LocalDateTime parseDate(String dateStr) {
        try {
            // 匹配格式: 2024/11/22-3pm
            Pattern pattern = Pattern.compile("(\\d{4})/(\\d{1,2})/(\\d{1,2})-(\\d{1,2})(am|pm)");
            Matcher matcher = pattern.matcher(dateStr);
            
            if (matcher.matches()) {
                int year = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int day = Integer.parseInt(matcher.group(3));
                int hour = Integer.parseInt(matcher.group(4));
                String ampm = matcher.group(5);
                
                // 处理12小时制
                if ("pm".equalsIgnoreCase(ampm) && hour < 12) {
                    hour += 12;
                } else if ("am".equalsIgnoreCase(ampm) && hour == 12) {
                    hour = 0;
                }
                
                return LocalDateTime.of(year, month, day, hour, 0);
            }
        } catch (DateTimeParseException | NumberFormatException e) {
            // 解析失败，返回null
        }
        
        return null;
    }
} 