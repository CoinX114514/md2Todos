package com.mdtotodos.controller;

import com.mdtotodos.model.MarkdownParser;
import com.mdtotodos.model.Task;
import com.mdtotodos.model.TaskExporter;
import com.mdtotodos.model.TaskExporter.ExportPlatform;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 任务控制器，负责协调模型和视图之间的交互
 */
public class TaskController {
    private final MarkdownParser parser;
    private final TaskExporter exporter;
    private List<Task> currentTasks;
    
    /**
     * 创建一个新的任务控制器
     */
    public TaskController() {
        this.parser = new MarkdownParser();
        this.exporter = new TaskExporter();
    }
    
    /**
     * 加载并解析Markdown文件中的任务
     * 
     * @param file Markdown文件
     * @return 成功解析的任务数量
     * @throws IOException 如果读取文件出错
     */
    public int loadTasks(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("文件不存在或无效");
        }
        
        currentTasks = parser.parseTasks(file);
        return currentTasks.size();
    }
    
    /**
     * 获取当前加载的任务列表
     * 
     * @return 任务列表
     */
    public List<Task> getCurrentTasks() {
        return currentTasks;
    }
    
    /**
     * 导出任务到指定平台
     * 
     * @param platform 导出平台类型
     * @param outputFile 输出文件（仅用于CSV和JSON导出）
     * @throws IOException 如果导出过程出错
     * @throws UnsupportedOperationException 如果选择的平台功能尚未实现
     */
    public void exportTasks(ExportPlatform platform, File outputFile) throws IOException, UnsupportedOperationException {
        if (currentTasks == null || currentTasks.isEmpty()) {
            throw new IllegalStateException("没有可导出的任务");
        }
        
        switch (platform) {
            case CSV:
                if (outputFile == null) {
                    throw new IllegalArgumentException("导出CSV时必须指定输出文件");
                }
                exporter.exportToCSV(currentTasks, outputFile);
                break;
                
            case JSON:
                if (outputFile == null) {
                    throw new IllegalArgumentException("导出JSON时必须指定输出文件");
                }
                exporter.exportToJSON(currentTasks, outputFile);
                break;
                
            case APPLE_REMINDERS:
                exporter.exportToAppleReminders(currentTasks);
                break;
                
            case MICROSOFT_TODO:
                exporter.exportToMicrosoftToDo(currentTasks);
                break;
                
            case GOOGLE_TASKS:
                exporter.exportToGoogleTasks(currentTasks);
                break;
                
            default:
                throw new UnsupportedOperationException("不支持的导出平台");
        }
    }
    
    /**
     * 检查当前系统是否支持特定的导出平台
     * 
     * @param platform 要检查的平台
     * @return 如果支持返回true
     */
    public boolean isPlatformSupported(ExportPlatform platform) {
        if (platform == ExportPlatform.APPLE_REMINDERS) {
            String os = System.getProperty("os.name").toLowerCase();
            return os.contains("mac");
        }
        
        // CSV和JSON在所有平台上都支持
        return platform == ExportPlatform.CSV || platform == ExportPlatform.JSON;
    }
} 