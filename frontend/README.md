# 个人博客 - 前端项目

基于 Vue 3 + TypeScript + Vite 构建的现代化博客前端。

## 技术栈

- Vue 3 (Composition API)
- TypeScript
- Vite
- Vue Router
- Pinia
- Axios
- Lucide Icons

## 设计风格

- 极简主义设计
- 初音绿（葱绿色）+ 青色系主题色
- 线条划分层次

## 开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 代码检查
npm run lint

# 代码格式化
npm run format
```

## 项目结构

```
src/
├── api/              # API 接口
├── assets/           # 静态资源
│   └── styles/      # 样式文件
├── components/       # 组件
│   ├── article/     # 文章相关组件
│   ├── common/      # 通用组件
│   ├── home/        # 首页组件
│   └── layout/      # 布局组件
├── router/          # 路由配置
├── stores/          # 状态管理
├── types/           # TypeScript 类型
├── views/           # 页面组件
├── App.vue          # 根组件
└── main.ts          # 入口文件
```

## 代码规范

详见 [CODE_STYLE.md](./CODE_STYLE.md)
