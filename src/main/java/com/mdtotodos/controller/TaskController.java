package com.mdtotodos.controller;

import com.mdtotodos.model.DocumentParser;
import com.mdtotodos.model.DocumentParserFactory;
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
    private final TaskExporter exporter;
    private List<Task> currentTasks;
    
    /**
     * 创建一个新的任务控制器
     */
    public TaskController() {
        this.exporter = new TaskExporter();
    }
    
    /**
     * 加载并解析文档中的任务
     * 
     * @param file 文档文件 (支持.md, .txt, .docx)
     * @return 成功解析的任务数量
     * @throws IOException 如果读取文件出错
     */
    public int loadTasks(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("文件不存在或无效");
        }
        
        DocumentParser parser = DocumentParserFactory.getParserForFile(file);
        if (parser == null) {
            throw new IOException("不支持的文件格式: " + getFileExtension(file));
        }
        
        currentTasks = parser.parseTasks(file);
        return currentTasks.size();
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param file 文件
     * @return 文件扩展名，不含点
     */
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return name.substring(lastDotIndex + 1);
        }
        return "";
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
     * 导出任务到ICS文件
     * 
     * @param outputFile 输出文件
     * @throws IOException 如果导出过程出错
     */
    public void exportTasksToICS(File outputFile) throws IOException {
        if (currentTasks == null || currentTasks.isEmpty()) {
            throw new IllegalStateException("没有可导出的任务");
        }
        
        if (outputFile == null) {
            throw new IllegalArgumentException("导出ICS时必须指定输出文件");
        }
        
        exporter.exportToICS(currentTasks, outputFile);
    }
    
    /**
     * 获取所有支持的文件扩展名
     * 
     * @return 支持的文件扩展名数组
     */
    public String[] getSupportedFileExtensions() {
        return DocumentParserFactory.getAllSupportedExtensions();
    }
    
    /**
     * 检查平台是否支持
     * 
     * @return 始终返回true，因为ICS在所有平台上都支持
     */
    public boolean isPlatformSupported() {
        return true;
    }
} 