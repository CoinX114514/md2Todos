package com.mdtotodos.app;

import com.mdtotodos.controller.TaskController;
import com.mdtotodos.model.Task;
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
        boolean listOnly = false;
        
        for (int i = 0; i < args.length; i++) {
            if (i == 0 && !args[i].startsWith("--")) {
                // 第一个非选项参数视为输入文件
                inputFile = args[i];
            } else if (args[i].equals("--output") && i + 1 < args.length) {
                // 输出文件选项
                outputFile = args[++i];
            } else if (args[i].equals("--list")) {
                // 仅列出任务，不导出
                listOnly = true;
            } else if (args[i].equals("--help")) {
                // 显示帮助
                showHelp();
                return;
            }
        }
        
        // 验证输入文件
        if (inputFile == null) {
            System.err.println("错误: 必须指定输入文件");
            showHelp();
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
                if (outputFile == null) {
                    // 如果未指定输出文件名，使用默认文件名
                    outputFile = "tasks.ics";
                    System.out.println("未指定输出文件，将使用默认文件: " + outputFile);
                }
                
                File output = new File(outputFile);
                controller.exportTasksToICS(output);
                
                System.out.println("成功导出任务到文件: " + outputFile);
            }
        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
            System.exit(1);
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
     * 显示帮助信息
     */
    private static void showHelp() {
        System.out.println("Markdown待办事项导入器 - ICS版本");
        System.out.println("用法: java -jar mdtotodos.jar <input-file> [options]");
        System.out.println();
        System.out.println("参数:");
        System.out.println("  <input-file>             输入文件 (.md, .txt, .docx)");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  --output <file>          指定输出ICS文件 (默认: tasks.ics)");
        System.out.println("  --list                   仅列出任务，不导出");
        System.out.println("  --help                   显示此帮助信息");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java -jar mdtotodos.jar tasks.md --output calendar.ics");
        System.out.println("  java -jar mdtotodos.jar tasks.docx --output calendar.ics");
        System.out.println("  java -jar mdtotodos.jar tasks.md --list");
    }
} 