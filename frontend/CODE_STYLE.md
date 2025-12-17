# 前端代码规范

## 技术栈

- Vue 3 (Composition API)
- TypeScript
- Vite
- Lucide Icons

## 代码风格

### 命名规范

- **组件名称**：使用 PascalCase，多个单词组成
  ```typescript
  // ✅ 正确
  BlogCard.vue
  ArticleList.vue
  
  // ❌ 错误
  blogcard.vue
  article-list.vue
  ```

- **文件名**：组件使用 PascalCase，工具函数使用 camelCase
  ```
  components/BlogCard.vue
  utils/formatDate.ts
  composables/useAuth.ts
  ```

- **变量和函数**：使用 camelCase
  ```typescript
  const userName = 'John'
  const fetchArticleList = () => {}
  ```

- **常量**：使用 UPPER_SNAKE_CASE
  ```typescript
  const API_BASE_URL = 'https://api.example.com'
  const MAX_RETRY_COUNT = 3
  ```

- **类型和接口**：使用 PascalCase
  ```typescript
  interface UserInfo {
    id: number
    name: string
  }
  
  type ArticleStatus = 'draft' | 'published'
  ```

### Vue 组件规范

- 使用 Composition API + `<script setup>`
- Props 和 Emits 使用 TypeScript 类型定义
- 组件顺序：template → script → style

```vue
<template>
  <div class="blog-card">
    <h3>{{ title }}</h3>
    <p>{{ content }}</p>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string
  content: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  click: [id: number]
}>()
</script>

<style scoped>
.blog-card {
  /* 样式 */
}
</style>
```

### TypeScript 规范

- 优先使用 `interface` 定义对象类型
- 使用 `type` 定义联合类型、交叉类型
- 避免使用 `any`，必要时使用 `unknown`
- 为函数参数和返回值添加类型注解

```typescript
// ✅ 推荐
interface Article {
  id: number
  title: string
  content: string
  createdAt: Date
}

type ArticleStatus = 'draft' | 'published' | 'archived'

function getArticle(id: number): Promise<Article> {
  // ...
}

// ❌ 避免
function getData(params: any): any {
  // ...
}
```

### 目录结构

```
frontend/
├── src/
│   ├── assets/          # 静态资源
│   │   ├── images/
│   │   └── styles/
│   ├── components/      # 公共组件
│   │   ├── common/      # 通用组件
│   │   └── layout/      # 布局组件
│   ├── views/           # 页面组件
│   ├── router/          # 路由配置
│   ├── stores/          # 状态管理 (Pinia)
│   ├── api/             # API 接口
│   ├── utils/           # 工具函数
│   ├── composables/     # 组合式函数
│   ├── types/           # TypeScript 类型定义
│   ├── App.vue
│   └── main.ts
├── public/              # 公共静态资源
├── .eslintrc.cjs        # ESLint 配置
├── .prettierrc.json     # Prettier 配置
├── tsconfig.json        # TypeScript 配置
├── vite.config.ts       # Vite 配置
└── package.json
```

### 样式规范

#### 基本原则

- 使用 `scoped` 避免样式污染
- 采用 BEM 命名规范（Block__Element--Modifier）
- 使用 CSS 变量定义主题色和通用样式
- 样式代码不超过 150 行，超过需拆分到独立样式文件

#### 通用样式变量

在 `src/assets/styles/variables.css` 中定义全局变量：

```css
:root {
  /* 主题色 - 粉色系 */
  --color-pink-50: #fff5f7;
  --color-pink-100: #ffe0e8;
  --color-pink-200: #ffc1d4;
  --color-pink-300: #ff9dbf;
  --color-pink-400: #ff6b9d;
  --color-pink-500: #ff3d7f;
  --color-pink-600: #e6005c;

  /* 主题色 - 蓝色系 */
  --color-blue-50: #f0f7ff;
  --color-blue-100: #d9ebff;
  --color-blue-200: #b3d9ff;
  --color-blue-300: #80c3ff;
  --color-blue-400: #4a90e2;
  --color-blue-500: #2563eb;
  --color-blue-600: #1e40af;

  /* 中性色 */
  --color-white: #ffffff;
  --color-black: #000000;
  --color-gray-50: #f9fafb;
  --color-gray-100: #f3f4f6;
  --color-gray-200: #e5e7eb;
  --color-gray-300: #d1d5db;
  --color-gray-400: #9ca3af;
  --color-gray-500: #6b7280;
  --color-gray-600: #4b5563;
  --color-gray-700: #374151;
  --color-gray-800: #1f2937;
  --color-gray-900: #111827;

  /* 间距 */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
  --spacing-2xl: 48px;
  --spacing-3xl: 64px;

  /* 字体大小 */
  --text-xs: 12px;
  --text-sm: 14px;
  --text-base: 16px;
  --text-lg: 18px;
  --text-xl: 20px;
  --text-2xl: 24px;
  --text-3xl: 30px;
  --text-4xl: 36px;

  /* 字重 */
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;

  /* 过渡 */
  --transition-fast: 150ms ease-in-out;
  --transition-base: 250ms ease-in-out;
  --transition-slow: 350ms ease-in-out;

  /* Z-index 层级 */
  --z-dropdown: 1000;
  --z-sticky: 1020;
  --z-fixed: 1030;
  --z-modal-backdrop: 1040;
  --z-modal: 1050;
  --z-popover: 1060;
  --z-tooltip: 1070;
}
```

#### 极简设计原则

**双色系统**
- 粉色系：用于主要强调、CTA 按钮、重要标记
- 蓝色系：用于链接、次要强调、交互反馈

**线条使用**
```css
/* 分隔线 - 使用纯色，不使用渐变 */
.divider {
  height: 1px;
  background: var(--color-gray-200);
}

/* 垂直强调线 - 粉色用于重要内容 */
.accent-line-pink {
  width: 2px;
  background: var(--color-pink-500);
}

/* 垂直强调线 - 蓝色用于次要内容 */
.accent-line-blue {
  width: 2px;
  background: var(--color-blue-500);
}

/* 边框 */
.bordered {
  border: 1px solid var(--color-gray-200);
}
```

**悬停效果**
```css
/* 使用线条延伸而非阴影 */
.card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 2px;
  height: 0;
  background: var(--color-pink-500); /* 或 var(--color-blue-500) */
  transition: height var(--transition-base);
}

.card:hover::before {
  height: 100%;
}
```

**避免使用**
- 渐变色背景（除非必要）
- 过度的阴影效果
- 复杂的卡片样式
- 过多的圆角

#### 组件样式示例

```vue
<style scoped>
/* 极简卡片 - 使用线条而非阴影 */
.blog-card {
  padding: var(--spacing-lg) 0 var(--spacing-lg) var(--spacing-md);
  border: 1px solid var(--color-gray-200);
  background: var(--color-white);
  transition: all var(--transition-base);
  position: relative;
}

/* 左侧强调线 */
.blog-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 2px;
  height: 0;
  background: var(--color-pink-500);
  transition: height var(--transition-base);
}

.blog-card:hover::before {
  height: 100%;
}

.blog-card__title {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--color-gray-900);
  margin-bottom: var(--spacing-md);
}

.blog-card__content {
  font-size: var(--text-base);
  color: var(--color-gray-600);
  line-height: 1.8;
}

/* 分隔线 */
.blog-card__divider {
  height: 1px;
  background: var(--color-gray-200);
  margin-top: var(--spacing-lg);
}
</style>
```

### 图标使用

使用 Lucide 图标库：

```vue
<script setup lang="ts">
import { Home, User, Settings } from 'lucide-vue-next'
</script>

<template>
  <Home :size="24" />
  <User :size="20" color="#ff6b9d" />
</template>
```

## 代码提交规范

使用 Conventional Commits 规范：

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
perf: 性能优化
test: 测试相关
chore: 构建/工具链相关
```

示例：
```
feat: 添加文章列表页面
fix: 修复深色模式下的样式问题
docs: 更新 README 文档
```

## 代码行数规范

### 组件文件

- **单个组件文件**：不超过 300 行（包含 template、script、style）
- **template 部分**：不超过 150 行
- **script 部分**：不超过 200 行
- **style 部分**：不超过 150 行

超过限制时的处理方式：
1. 拆分为多个子组件
2. 提取逻辑到 composables
3. 提取样式到独立的 CSS 文件

### 函数和方法

- **单个函数**：不超过 50 行
- **复杂函数**：超过 30 行需添加详细注释
- **参数数量**：不超过 5 个，超过使用对象传参

```typescript
// ❌ 避免：参数过多
function createArticle(title: string, content: string, author: string, tags: string[], status: string) {}

// ✅ 推荐：使用对象
interface CreateArticleParams {
  title: string
  content: string
  author: string
  tags: string[]
  status: string
}

function createArticle(params: CreateArticleParams) {}
```

### 文件大小

- **工具函数文件**：不超过 200 行
- **API 文件**：按模块拆分，单文件不超过 300 行
- **类型定义文件**：按功能模块拆分，单文件不超过 200 行

### 代码复杂度

- **圈复杂度**：单个函数不超过 10
- **嵌套层级**：不超过 4 层
- **三元表达式**：不嵌套使用

```typescript
// ❌ 避免：嵌套三元表达式
const result = condition1 ? value1 : condition2 ? value2 : value3

// ✅ 推荐：使用 if-else 或提前返回
function getResult() {
  if (condition1) return value1
  if (condition2) return value2
  return value3
}
```

## 注释规范

### 组件注释

```vue
<script setup lang="ts">
/**
 * 博客卡片组件
 * @description 用于展示博客文章的卡片，支持点击跳转和收藏功能
 * @author [your-name]
 * @date 2024-01-01
 */

interface Props {
  /** 文章 ID */
  id: number
  /** 文章标题 */
  title: string
  /** 文章摘要 */
  summary: string
  /** 是否已收藏 */
  isFavorite?: boolean
}
</script>
```

### 函数注释

```typescript
/**
 * 格式化日期
 * @param date - 日期对象或时间戳
 * @param format - 格式化模板，默认 'YYYY-MM-DD'
 * @returns 格式化后的日期字符串
 * @example
 * formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss')
 * // => '2024-01-01 12:00:00'
 */
function formatDate(date: Date | number, format = 'YYYY-MM-DD'): string {
  // 实现...
}
```

### 复杂逻辑注释

```typescript
// 计算文章阅读时间（按每分钟 200 字计算）
const readingTime = Math.ceil(content.length / 200)

// TODO: 需要优化性能，考虑使用虚拟滚动
// FIXME: 修复深色模式下的样式问题
// NOTE: 此处逻辑与后端保持一致
```

## 最佳实践

### 1. 组件拆分原则

- **单一职责**：一个组件只做一件事
- **行数限制**：超过 300 行必须拆分
- **可复用性**：通用组件放在 `components/common`
- **业务组件**：特定页面组件放在对应的 `views` 目录

### 2. 性能优化

- 使用 `v-memo` 缓存复杂列表项
- 使用 `v-once` 渲染静态内容
- 大列表使用虚拟滚动
- 图片懒加载
- 路由懒加载

```typescript
// 路由懒加载
const routes = [
  {
    path: '/article',
    component: () => import('@/views/Article.vue')
  }
]
```

### 3. 错误处理

- 统一的错误处理和提示机制
- API 请求必须有错误处理
- 使用 try-catch 包裹可能出错的代码

```typescript
async function fetchArticle(id: number) {
  try {
    const data = await api.getArticle(id)
    return data
  } catch (error) {
    console.error('获取文章失败:', error)
    // 显示错误提示
    showError('获取文章失败，请稍后重试')
    return null
  }
}
```

### 4. 代码复用

- 提取公共逻辑到 composables
- 提取公共工具函数到 utils
- 避免重复代码

```typescript
// composables/useArticle.ts
export function useArticle() {
  const article = ref<Article | null>(null)
  const loading = ref(false)

  async function fetchArticle(id: number) {
    loading.value = true
    try {
      article.value = await api.getArticle(id)
    } finally {
      loading.value = false
    }
  }

  return {
    article,
    loading,
    fetchArticle
  }
}
```

### 5. 类型安全

- 充分利用 TypeScript 类型系统
- 避免使用 `any`
- 为 API 响应定义类型
- 使用类型守卫

```typescript
// 类型守卫
function isArticle(data: unknown): data is Article {
  return (
    typeof data === 'object' &&
    data !== null &&
    'id' in data &&
    'title' in data
  )
}
```

### 6. 可访问性

- 使用语义化 HTML 标签
- 添加 ARIA 属性
- 确保键盘可访问
- 提供替代文本

```vue
<template>
  <button
    class="glass-button"
    aria-label="收藏文章"
    @click="handleFavorite"
  >
    <Heart :size="20" />
  </button>

  <img
    :src="imageUrl"
    :alt="imageAlt"
    loading="lazy"
  />
</template>
```

## 开发工具推荐

- VS Code
- Volar (Vue Language Features)
- ESLint
- Prettier
- Vue DevTools
