#!/bin/bash

# 应用信息
APP_NAME="Markdown待办事项导入器"
APP_VERSION="1.0.0"
OUTPUT_DIR="release"

# 创建输出目录
mkdir -p "$OUTPUT_DIR"

# 确保dist目录中有所有需要的文件
if [ ! -f "dist/mdtotodos-1.0.0.jar" ]; then
    echo "错误: 未找到主程序JAR文件"
    exit 1
fi

# 为不同平台创建压缩包
echo "正在为Windows创建ZIP包..."
zip -r "$OUTPUT_DIR/${APP_NAME}_Windows_${APP_VERSION}.zip" dist -x "*.DS_Store" "*.git*"

echo "正在为macOS/Linux创建ZIP包..."
ZIP_FILE="$OUTPUT_DIR/${APP_NAME}_macOS-Linux_${APP_VERSION}.zip"
zip -r "$ZIP_FILE" dist -x "*.DS_Store" "*.git*" "*.bat"

echo "打包完成！"
echo "Windows版本: ${OUTPUT_DIR}/${APP_NAME}_Windows_${APP_VERSION}.zip"
echo "macOS/Linux版本: $ZIP_FILE" 