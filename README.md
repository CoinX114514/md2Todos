# Markdown待办事项导入器 (Java MVC版)

这是一个使用Java MVC模式开发的跨平台工具，可以将Markdown文件中的待办事项导入到各种任务管理系统中。支持Windows、macOS和Linux系统。

## 📋 功能特点

- 从Markdown文件中提取待办事项
- 支持任务标题、描述和截止日期
- 跨平台支持 (Windows, macOS, Linux)
- 提供命令行和图形用户界面
- 基于Java MVC架构设计，易于扩展
- 多种任务管理系统导出选项

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
2. 选择导出平台(CSV, JSON, Apple提醒事项等)
3. 点击"预览任务"查看解析结果
4. 点击"导出任务"完成导出

### 命令行版本

如果您习惯使用命令行，可以使用以下命令:

```bash
java -jar target/mdtotodos-1.0.0.jar input.md [options]
```

### 支持的平台参数:

- `csv` - 导出到CSV文件
- `json` - 导出到JSON文件
- `apple` - 导出到Apple提醒事项 (仅限macOS)
- `microsoft` - 导出到Microsoft To Do
- `google` - 导出到Google Tasks

### 命令行选项:

```
--platform <platform>  指定导出平台 (csv, json, apple, microsoft, google)
--output <file>        指定输出文件 (仅用于CSV和JSON导出)
--list                 仅列出任务，不导出
--help                 显示帮助信息
```

### 命令行示例:

```bash
# 导出到CSV文件
java -jar target/mdtotodos-1.0.0.jar example.md --platform csv --output tasks.csv

# 在macOS上导出到Apple提醒事项
java -jar target/mdtotodos-1.0.0.jar example.md --platform apple

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
4. **平台特定问题**：
   - macOS: 确保已授予Java应用访问提醒事项的权限
   - Windows/Google集成: 确保已完成OAuth2验证

## 📜 许可证

此工具使用MIT许可证开源。

## 🙏 感谢

感谢使用本工具！欢迎提交问题和贡献代码。

---

*最后更新：2023年11月* 