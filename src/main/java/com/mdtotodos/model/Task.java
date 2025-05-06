package com.mdtotodos.model;

import java.time.LocalDateTime;

/**
 * 表示一个待办事项任务
 */
public class Task {
    private String title;
    private String description;
    private LocalDateTime dueDate;

    /**
     * 创建一个新的任务
     * 
     * @param title 任务标题（必需）
     * @param description 任务描述（可选）
     * @param dueDate 截止日期（可选）
     */
    public Task(String title, String description, LocalDateTime dueDate) {
        this.title = title != null ? title.trim() : "";
        this.description = description != null ? description.trim() : "";
        this.dueDate = dueDate;
    }

    /**
     * 创建一个只有标题的任务
     * 
     * @param title 任务标题
     */
    public Task(String title) {
        this(title, "", null);
    }

    /**
     * 获取任务标题
     * 
     * @return 任务标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置任务标题
     * 
     * @param title 任务标题
     */
    public void setTitle(String title) {
        this.title = title != null ? title.trim() : "";
    }

    /**
     * 获取任务描述
     * 
     * @return 任务描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置任务描述
     * 
     * @param description 任务描述
     */
    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    /**
     * 获取截止日期
     * 
     * @return 截止日期
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * 设置截止日期
     * 
     * @param dueDate 截止日期
     */
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * 任务是否有截止日期
     * 
     * @return 如果有截止日期则返回true
     */
    public boolean hasDueDate() {
        return dueDate != null;
    }

    /**
     * 任务是否有描述
     * 
     * @return 如果有描述则返回true
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("标题: ").append(title);
        
        if (hasDescription()) {
            sb.append(", 描述: ").append(description);
        }
        
        if (hasDueDate()) {
            sb.append(", 截止日期: ").append(dueDate);
        }
        
        return sb.toString();
    }
} 