package com.mdtotodos.model;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCX解析器，用于从Word文档中提取待办事项
 */
public class DocxParser implements DocumentParser {
    // 任务基本格式的正则表达式: "数字. 任务内容"
    private static final Pattern TASK_PATTERN = Pattern.compile("^\\s*(\\d+)\\.\\s+(.+)$");
    
    // 日期格式的正则表达式: YYYY/MM/DD-HHam/pm
    private static final Pattern DATE_PATTERN = 
            Pattern.compile("(\\d{4}/\\d{1,2}/\\d{1,2}-\\d{1,2}(?:am|pm))");
    
    // 描述部分的正则表达式: "// 描述内容"
    private static final Pattern DESCRIPTION_PATTERN = 
            Pattern.compile("\\s*//\\s*(.*?)\\s*(?:" + DATE_PATTERN.pattern() + ")?$");

    /**
     * 从DOCX文件中解析待办事项
     * 
     * @param file DOCX文件
     * @return 解析出的任务列表
     * @throws IOException 如果读取文件出错
     */
    @Override
    public List<Task> parseTasks(File file) throws IOException {
        List<Task> tasks = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                
                // 跳过空行或标题样式
                if (text == null || text.trim().isEmpty() || isHeadingStyle(paragraph)) {
                    continue;
                }
                
                Task task = parseTaskLine(text);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }
        
        return tasks;
    }
    
    /**
     * 获取支持的文件扩展名
     * 
     * @return 支持的文件扩展名数组
     */
    @Override
    public String[] getSupportedExtensions() {
        return new String[] {"docx"};
    }
    
    /**
     * 判断段落是否为标题样式
     * 
     * @param paragraph 段落
     * @return 如果是标题样式则返回true
     */
    private boolean isHeadingStyle(XWPFParagraph paragraph) {
        // 获取段落的样式名称
        String styleName = paragraph.getStyle();
        
        // 检查是否是标题样式
        if (styleName != null) {
            return styleName.toLowerCase().contains("heading") || 
                   styleName.toLowerCase().contains("title");
        }
        
        // 另一种判断方法：检查段落的大小
        if (paragraph.getRuns().size() > 0) {
            int fontSize = paragraph.getRuns().get(0).getFontSize();
            // 通常标题字体更大
            return fontSize > 14;
        }
        
        return false;
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
        } catch (Exception e) {
            // 解析失败，返回null
        }
        
        return null;
    }
} 