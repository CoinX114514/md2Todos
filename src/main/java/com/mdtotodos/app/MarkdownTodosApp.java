package com.mdtotodos.app;

import com.mdtotodos.controller.TaskController;
import com.mdtotodos.model.Task;
import com.mdtotodos.model.TaskExporter.ExportPlatform;
import com.mdtotodos.view.MainView;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 应用程序主类，包含命令行入口和GUI入口
 */
public class MarkdownTodosApp {
    
    /**
     * 应用程序主入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // 无参数启动GUI
            launchGUI();
        } else {
            // 有参数使用命令行处理
            processCommandLine(args);
        }
    }
    
    /**
     * 启动GUI界面
     */
    private static void launchGUI() {
        MainView.main(new String[0]);
    }
    
    /**
     * 处理命令行参数
     * 
     * @param args 命令行参数
     */
    private static void processCommandLine(String[] args) {
        // 参数解析
        String inputFile = null;
        String outputFile = null;
        ExportPlatform platform = ExportPlatform.CSV; // 默认为CSV
        boolean listOnly = false;
        
        for (int i = 0; i < args.length; i++) {
            if (i == 0 && !args[i].startsWith("--")) {
                // 第一个非选项参数视为输入文件
                inputFile = args[i];
            } else if (args[i].equals("--platform") && i + 1 < args.length) {
                // 平台选项
                String platformName = args[++i].toLowerCase();
                platform = parsePlatform(platformName);
            } else if (args[i].equals("--output") && i + 1 < args.length) {
                // 输出文件选项
                outputFile = args[++i];
            } else if (args[i].equals("--list")) {
                // 仅列出任务，不导出
                listOnly = true;
            } else if (args[i].equals("--help")) {
                // 显示帮助
                printHelp();
                return;
            }
        }
        
        // 验证输入文件
        if (inputFile == null) {
            System.err.println("错误: 必须指定输入文件");
            printHelp();
            System.exit(1);
        }
        
        File mdFile = new File(inputFile);
        if (!mdFile.exists()) {
            System.err.println("错误: 找不到文件: " + inputFile);
            System.exit(1);
        }
        
        // 执行操作
        try {
            TaskController controller = new TaskController();
            int taskCount = controller.loadTasks(mdFile);
            System.out.println("从文件 " + inputFile + " 中解析出 " + taskCount + " 个任务");
            
            if (listOnly) {
                // 仅列出任务
                printTasks(controller.getCurrentTasks());
            } else {
                // 导出任务
                if ((platform == ExportPlatform.CSV || platform == ExportPlatform.JSON) && outputFile == null) {
                    // 如果输出到文件但未指定文件名，使用默认文件名
                    String extension = (platform == ExportPlatform.CSV) ? ".csv" : ".json";
                    outputFile = "tasks" + extension;
                    System.out.println("未指定输出文件，将使用默认文件: " + outputFile);
                }
                
                File output = (outputFile != null) ? new File(outputFile) : null;
                controller.exportTasks(platform, output);
                
                if (platform == ExportPlatform.CSV || platform == ExportPlatform.JSON) {
                    System.out.println("成功导出任务到文件: " + outputFile);
                } else {
                    System.out.println("成功导出任务");
                }
            }
        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
            System.exit(1);
        } catch (UnsupportedOperationException e) {
            System.err.println("不支持的操作: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * 解析平台名称
     * 
     * @param platformName 平台名称
     * @return 对应的平台枚举值
     */
    private static ExportPlatform parsePlatform(String platformName) {
        switch (platformName) {
            case "csv":
                return ExportPlatform.CSV;
            case "json":
                return ExportPlatform.JSON;
            case "apple":
                return ExportPlatform.APPLE_REMINDERS;
            case "microsoft":
                return ExportPlatform.MICROSOFT_TODO;
            case "google":
                return ExportPlatform.GOOGLE_TASKS;
            default:
                System.err.println("警告: 未知的平台 '" + platformName + "', 将使用默认平台 (CSV)");
                return ExportPlatform.CSV;
        }
    }
    
    /**
     * 打印任务列表
     * 
     * @param tasks 任务列表
     */
    private static void printTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("未找到任务");
            return;
        }
        
        System.out.println("任务列表 (" + tasks.size() + " 个任务):");
        System.out.println("------------------------------------------");
        
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println((i + 1) + ". " + task.getTitle());
            
            if (task.hasDescription()) {
                System.out.println("   描述: " + task.getDescription());
            }
            
            if (task.hasDueDate()) {
                System.out.println("   截止日期: " + task.getDueDate());
            }
            
            System.out.println();
        }
    }
    
    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("Markdown待办事项导入器");
        System.out.println("用法: java -jar mdtotodos.jar input.md [选项]");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  --platform <platform>  指定导出平台 (csv, json, apple, microsoft, google)");
        System.out.println("  --output <file>        指定输出文件 (仅用于CSV和JSON导出)");
        System.out.println("  --list                 仅列出任务，不导出");
        System.out.println("  --help                 显示此帮助信息");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java -jar mdtotodos.jar example.md --platform csv --output tasks.csv");
        System.out.println("  java -jar mdtotodos.jar example.md --platform apple");
        System.out.println("  java -jar mdtotodos.jar example.md --list");
    }
} 