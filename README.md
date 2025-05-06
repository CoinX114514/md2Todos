# Markdown待办事项导入器 (ICS日历版)

这是一个使用Java MVC模式开发的跨平台工具，可以将Markdown文件中的待办事项导出为ICS日历文件，方便导入到各种日历应用中。支持Windows、macOS和Linux系统。

## 📋 功能特点

- 从Markdown文件中提取待办事项
- 支持任务标题、描述和截止日期
- 跨平台支持 (Windows, macOS, Linux)
- 提供命令行和图形用户界面
- 基于Java MVC架构设计
- 生成标准ICS日历文件，可导入任何支持iCalendar格式的应用

## 🔧 安装与构建

### 前提条件
- Java JDK 8+
- Maven 3.6+

### 构建步骤

```bash
# 克隆仓库
git clone https://github.com/yourusername/mdToTodos.git
cd mdToTodos

# 使用Maven构建
mvn clean package
```

构建完成后，将在`target`目录下生成可执行JAR文件：`mdtotodos-1.0.0.jar`

## 🚀 使用方法

### 图形界面版本

最简单的使用方式是运行GUI版本:

```bash
java -jar target/mdtotodos-1.0.0.jar
```

GUI界面使用步骤:
1. 点击"浏览..."选择Markdown文件
2. 设置ICS输出文件路径
3. 点击"预览任务"查看解析结果
4. 点击"导出到ICS"完成导出

### 命令行版本

如果您习惯使用命令行，可以使用以下命令:

```bash
java -jar target/mdtotodos-1.0.0.jar input.md [选项]
```

### 命令行选项:

```
--output <file>        指定输出ICS文件 (默认为tasks.ics)
--list                 仅列出任务，不导出
--help                 显示帮助信息
```

### 命令行示例:

```bash
# 导出到ICS日历文件
java -jar target/mdtotodos-1.0.0.jar example.md --output calendar.ics

# 仅列出解析出的任务，不导出
java -jar target/mdtotodos-1.0.0.jar example.md --list
```

## ✍️ Markdown格式说明

您的Markdown文件应按照以下格式编写待办事项：

```
1. 任务标题 // 可选的任务描述 YYYY/MM/DD-HHam/pm
```

### 示例：

```
1. 完成项目报告 // 需要包含财务部分 2024/11/22-3pm
2. 购买牛奶
3. 预约医生 // 询问过敏症状 2023/12/15-10am
```

### 格式解释：

- **任务编号**：使用数字后跟点和空格（如"1. "）开始每个任务
- **任务标题**：必填，将显示为任务的主要内容
- **任务描述**：可选，使用 `//` 分隔后添加，将显示在任务的备注中
- **截止日期**：可选，格式为 `YYYY/MM/DD-HHam/pm`（如 `2024/11/22-3pm`）

## 📝 示例文件

参见 `example.md` 文件作为参考模板。

## 📅 关于ICS日历文件

ICS（iCalendar）是一种标准的日历文件格式，被广泛应用于各种日历应用程序。导出的ICS文件可以：

- 导入到Apple日历、Google日历、Microsoft Outlook等应用
- 包含任务的标题、详细描述和截止日期
- 将有截止日期的任务创建为日历事件（VEVENT）
- 将无截止日期的任务创建为待办事项（VTODO）
- 自动设置提前15分钟的提醒
- 轻松与他人共享任务列表

使用方法：
1. 将Markdown任务导出为ICS文件
2. 在日历应用中选择"导入"或"添加日历"
3. 选择生成的ICS文件
4. 任务将作为待办事项或事件显示在您的日历中

## ⚙️ 技术细节

此应用使用Java构建，采用MVC架构模式：
- **Model**: 负责数据处理和业务逻辑
- **View**: 提供用户界面，基于Java Swing实现
- **Controller**: 协调Model和View的交互

| 组件 | 主要类 |
|------|--------|
| Model | `Task`, `MarkdownParser`, `TaskExporter` |
| View | `MainView` |
| Controller | `TaskController` |
| 应用入口 | `MarkdownTodosApp` |

## 🛠️ 故障排除

如果在使用过程中遇到问题：

1. **确保格式正确**：检查您的Markdown文件是否按照正确的格式编写
2. **检查Java版本**：确保使用Java 8或更高版本
3. **构建问题**：如果构建失败，确保Maven安装正确且版本满足要求
4. **ICS导出问题**：如果导入日历应用时出现格式错误，请检查任务的截止日期格式是否正确

## 📜 许可证

此工具使用MIT许可证开源。

## 🙏 感谢

感谢使用本工具！欢迎提交问题和贡献代码。

---

*最后更新：2023年11月* 