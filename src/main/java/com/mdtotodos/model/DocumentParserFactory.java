package com.mdtotodos.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文档解析器工厂，用于获取合适的文档解析器
 */
public class DocumentParserFactory {
    private static final List<DocumentParser> PARSERS = new ArrayList<>();
    
    // 静态初始化解析器列表
    static {
        PARSERS.add(new MarkdownParser());
        PARSERS.add(new DocxParser());
    }
    
    /**
     * 获取适合处理给定文件的解析器
     * 
     * @param file 要解析的文件
     * @return 合适的解析器，如果没有找到则返回null
     */
    public static DocumentParser getParserForFile(File file) {
        if (file == null) {
            return null;
        }
        
        String fileName = file.getName().toLowerCase();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex <= 0) {
            return null; // 没有扩展名
        }
        
        String extension = fileName.substring(lastDotIndex + 1);
        
        for (DocumentParser parser : PARSERS) {
            if (Arrays.asList(parser.getSupportedExtensions()).contains(extension)) {
                return parser;
            }
        }
        
        return null; // 没有找到合适的解析器
    }
    
    /**
     * 获取所有支持的文件扩展名列表
     * 
     * @return 所有支持的文件扩展名数组
     */
    public static String[] getAllSupportedExtensions() {
        List<String> extensions = new ArrayList<>();
        for (DocumentParser parser : PARSERS) {
            extensions.addAll(Arrays.asList(parser.getSupportedExtensions()));
        }
        return extensions.toArray(new String[0]);
    }
} 