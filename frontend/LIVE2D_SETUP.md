# Live2D 组件设置说明

## 安装依赖

在 `frontend` 目录下运行以下命令安装 Live2D 所需的依赖：

```bash
npm install pixi.js@7 pixi-live2d-display
```

依赖已自动安装完成！

## 功能特性

Live2D 悬浮组件已创建，包含以下功能：

1. **模型渲染** - 使用 PIXI.js 和 Live2D SDK 渲染 MIKU 模型
2. **拖拽移动** - 可以拖拽组件到屏幕任意位置，位置会保存到 localStorage
3. **鼠标跟随** - 模型会跟随鼠标移动视线
4. **表情切换** - 点击笑脸按钮可以切换不同表情
5. **显示/隐藏** - 点击眼睛按钮可以隐藏/显示模型
6. **响应式设计** - 在移动端会自动缩小尺寸

## 组件位置

- 主组件：`frontend/src/components/layout/Live2DWidget.vue`
- 类型声明：`frontend/src/types/live2d.d.ts`
- 已集成到：`frontend/src/App.vue`

## 使用方法

安装依赖后，直接运行项目即可：

```bash
npm run dev
```

Live2D 组件会自动显示在页面右下角。

## 自定义配置

如需调整模型大小、位置或其他参数，可以修改 `Live2DWidget.vue` 中的以下配置：

- `model.scale.set(0.15)` - 调整模型缩放比例
- `model.x = 150` 和 `model.y = 350` - 调整模型在画布中的位置
- `width: 300, height: 400` - 调整画布尺寸

## 故障排除

如果模型无法加载，请检查：

1. 依赖是否正确安装
2. 模型文件路径是否正确
3. 浏览器控制台是否有错误信息
4. Vite 配置是否正确处理静态资源
