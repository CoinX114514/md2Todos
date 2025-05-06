package com.mdtotodos.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 文档解析器接口，用于从不同格式的文档中提取待办事项
 */
public interface DocumentParser {
    
    /**
     * 从文档中解析待办事项
     * 
     * @param file 文档文件
     * @return 解析出的任务列表
     * @throws IOException 如果读取文件出错
     */
    List<Task> parseTasks(File file) throws IOException;
    
    /**
     * 获取支持的文件扩展名
     * 
     * @return 支持的文件扩展名数组，如 {"md", "txt"}
     */
    String[] getSupportedExtensions();
} 