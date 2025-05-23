package com.mdtotodos.view;

import com.mdtotodos.controller.TaskController;
import com.mdtotodos.model.Task;
import com.mdtotodos.model.TaskExporter.ExportPlatform;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 应用程序的主视图，使用Swing实现GUI界面
 */
public class MainView extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    
    // 控制器
    private final TaskController controller;
    
    // UI组件
    private JTextField inputFilePathField;
    private JTextField outputFilePathField;
    private JComboBox<String> platformComboBox;
    private JTextArea previewTextArea;
    private JTextArea logTextArea;
    private JPanel outputFilePanel;
    private JButton browseInputButton;
    private JButton browseOutputButton;
    private JButton previewButton;
    private JButton exportButton;
    
    // 导出平台名称和对应的枚举值
    private final String[] platformNames = {
            "CSV文件", "JSON文件", "Apple提醒事项 (仅限macOS)", 
            "Microsoft To Do", "Google Tasks"
    };
    private final ExportPlatform[] platformValues = {
            ExportPlatform.CSV, ExportPlatform.JSON, ExportPlatform.APPLE_REMINDERS,
            ExportPlatform.MICROSOFT_TODO, ExportPlatform.GOOGLE_TASKS
    };
    
    /**
     * 创建主视图
     */
    public MainView() {
        controller = new TaskController();
        initializeUI();
        setupPlatformOptions();
    }
    
    /**
     * 初始化用户界面
     */
    private void initializeUI() {
        setTitle("Markdown待办事项导入器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        setMinimumSize(new Dimension(600, 500));
        
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);
        
        // 创建标题标签
        JLabel titleLabel = new JLabel("Markdown待办事项导入器");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(titleLabel, BorderLayout.NORTH);
        
        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        // 输入文件选择面板
        JPanel inputFilePanel = new JPanel();
        inputFilePanel.setBorder(new TitledBorder("选择Markdown文件"));
        inputFilePanel.setLayout(new BoxLayout(inputFilePanel, BoxLayout.X_AXIS));
        inputFilePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        inputFilePathField = new JTextField();
        browseInputButton = new JButton("浏览...");
        
        inputFilePanel.add(inputFilePathField);
        inputFilePanel.add(Box.createHorizontalStrut(10));
        inputFilePanel.add(browseInputButton);
        
        mainPanel.add(inputFilePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 导出平台选择面板
        JPanel platformPanel = new JPanel();
        platformPanel.setBorder(new TitledBorder("选择导出平台"));
        platformPanel.setLayout(new BoxLayout(platformPanel, BoxLayout.X_AXIS));
        platformPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        platformComboBox = new JComboBox<>(platformNames);
        platformPanel.add(platformComboBox);
        
        mainPanel.add(platformPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 输出文件选择面板
        outputFilePanel = new JPanel();
        outputFilePanel.setBorder(new TitledBorder("输出文件"));
        outputFilePanel.setLayout(new BoxLayout(outputFilePanel, BoxLayout.X_AXIS));
        outputFilePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        outputFilePathField = new JTextField();
        browseOutputButton = new JButton("浏览...");
        
        outputFilePanel.add(outputFilePathField);
        outputFilePanel.add(Box.createHorizontalStrut(10));
        outputFilePanel.add(browseOutputButton);
        
        mainPanel.add(outputFilePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 预览区域
        JPanel previewPanel = new JPanel();
        previewPanel.setBorder(new TitledBorder("待办事项预览"));
        previewPanel.setLayout(new BorderLayout());
        
        previewTextArea = new JTextArea();
        previewTextArea.setEditable(false);
        JScrollPane previewScrollPane = new JScrollPane(previewTextArea);
        previewPanel.add(previewScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(previewPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 日志区域
        JPanel logPanel = new JPanel();
        logPanel.setBorder(new TitledBorder("处理日志"));
        logPanel.setLayout(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(0, 100));
        
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(logPanel);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        previewButton = new JButton("预览任务");
        exportButton = new JButton("导出任务");
        JButton exitButton = new JButton("退出");
        
        buttonPanel.add(previewButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(exitButton);
        
        // 设置事件监听器
        setupEventListeners();
    }
    
    /**
     * 设置平台选择选项
     */
    private void setupPlatformOptions() {
        // 根据当前操作系统禁用不支持的选项
        for (int i = 0; i < platformValues.length; i++) {
            if (!controller.isPlatformSupported(platformValues[i])) {
                platformComboBox.setEnabled(false);
            }
        }
        
        // 默认选择CSV
        platformComboBox.setSelectedIndex(0);
        updateUIForPlatform();
        
        // 添加平台选择变更监听器
        platformComboBox.addActionListener(e -> updateUIForPlatform());
    }
    
    /**
     * 根据所选平台更新UI
     */
    private void updateUIForPlatform() {
        int selectedIndex = platformComboBox.getSelectedIndex();
        ExportPlatform platform = platformValues[selectedIndex];
        
        // 仅对CSV和JSON导出显示输出文件选择
        boolean showOutputPanel = platform == ExportPlatform.CSV || platform == ExportPlatform.JSON;
        outputFilePanel.setVisible(showOutputPanel);
        
        // 设置默认输出文件名
        if (showOutputPanel) {
            String extension = platform == ExportPlatform.CSV ? ".csv" : ".json";
            String defaultPath = System.getProperty("user.dir") + File.separator + "tasks" + extension;
            outputFilePathField.setText(defaultPath);
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * 设置事件监听器
     */
    private void setupEventListeners() {
        // 浏览输入文件按钮
        browseInputButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("选择Markdown文件");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Markdown文件", "md", "txt"));
            
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                inputFilePathField.setText(selectedFile.getAbsolutePath());
                log("已选择文件: " + selectedFile.getAbsolutePath());
            }
        });
        
        // 浏览输出文件按钮
        browseOutputButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存文件");
            
            int platformIndex = platformComboBox.getSelectedIndex();
            if (platformIndex == 0) { // CSV
                fileChooser.setFileFilter(new FileNameExtensionFilter("CSV文件", "csv"));
                fileChooser.setSelectedFile(new File("tasks.csv"));
            } else if (platformIndex == 1) { // JSON
                fileChooser.setFileFilter(new FileNameExtensionFilter("JSON文件", "json"));
                fileChooser.setSelectedFile(new File("tasks.json"));
            }
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                outputFilePathField.setText(selectedFile.getAbsolutePath());
                log("将保存到: " + selectedFile.getAbsolutePath());
            }
        });
        
        // 预览按钮
        previewButton.addActionListener(e -> previewTasks());
        
        // 导出按钮
        exportButton.addActionListener(e -> exportTasks());
    }
    
    /**
     * 预览任务
     */
    private void previewTasks() {
        String inputPath = inputFilePathField.getText().trim();
        if (inputPath.isEmpty()) {
            showError("请先选择一个Markdown文件");
            return;
        }
        
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            showError("找不到文件: " + inputPath);
            return;
        }
        
        try {
            int taskCount = controller.loadTasks(inputFile);
            updatePreviewArea();
            log("从文件 " + inputPath + " 中解析出 " + taskCount + " 个任务");
        } catch (IOException ex) {
            showError("解析文件时出错: " + ex.getMessage());
            log("错误: " + ex.getMessage());
        }
    }
    
    /**
     * 更新预览区域
     */
    private void updatePreviewArea() {
        List<Task> tasks = controller.getCurrentTasks();
        previewTextArea.setText("");
        
        if (tasks == null || tasks.isEmpty()) {
            previewTextArea.setText("未找到任务或尚未解析文件。");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            sb.append(i + 1).append(". ").append(task.getTitle()).append("\n");
            
            if (task.hasDescription()) {
                sb.append("   描述: ").append(task.getDescription()).append("\n");
            }
            
            if (task.hasDueDate()) {
                sb.append("   截止日期: ").append(task.getDueDate().format(DATE_FORMATTER)).append("\n");
            }
            
            sb.append("\n");
        }
        
        previewTextArea.setText(sb.toString());
    }
    
    /**
     * 导出任务
     */
    private void exportTasks() {
        // 如果没有任务，先尝试加载
        if (controller.getCurrentTasks() == null || controller.getCurrentTasks().isEmpty()) {
            previewTasks();
            
            if (controller.getCurrentTasks() == null || controller.getCurrentTasks().isEmpty()) {
                showError("没有可导出的任务");
                return;
            }
        }
        
        // 获取选择的平台
        int platformIndex = platformComboBox.getSelectedIndex();
        ExportPlatform platform = platformValues[platformIndex];
        
        try {
            if (platform == ExportPlatform.CSV || platform == ExportPlatform.JSON) {
                String outputPath = outputFilePathField.getText().trim();
                if (outputPath.isEmpty()) {
                    showError("请指定输出文件路径");
                    return;
                }
                
                File outputFile = new File(outputPath);
                controller.exportTasks(platform, outputFile);
                log("成功导出任务到 " + outputPath);
                showSuccess("成功导出到" + (platform == ExportPlatform.CSV ? "CSV" : "JSON") + "文件:\n" + outputPath);
            } else {
                controller.exportTasks(platform, null);
                log("成功导出任务到 " + platformNames[platformIndex]);
                showSuccess("成功导出到" + platformNames[platformIndex]);
            }
        } catch (IOException ex) {
            showError("导出时出错: " + ex.getMessage());
            log("错误: " + ex.getMessage());
        } catch (UnsupportedOperationException ex) {
            showInfo(platformNames[platformIndex] + "功能需要额外配置", ex.getMessage());
            log(ex.getMessage());
        }
    }
    
    /**
     * 在日志区域添加消息
     * 
     * @param message 要添加的消息
     */
    private void log(String message) {
        logTextArea.append(message + "\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }
    
    /**
     * 显示错误对话框
     * 
     * @param message 错误消息
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * 显示成功对话框
     * 
     * @param message 成功消息
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 显示信息对话框
     * 
     * @param title 标题
     * @param message 信息消息
     */
    private void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 应用程序入口点
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            // 尝试使用系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // 如果失败，使用默认外观
        }
        
        EventQueue.invokeLater(() -> {
            try {
                MainView frame = new MainView();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 