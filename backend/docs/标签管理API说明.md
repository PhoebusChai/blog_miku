# 标签管理API说明

## 概述

标签管理模块提供了完整的增删改查功能，支持标签的创建、更新、删除和查询。

## API端点

### 1. 获取所有标签

**请求**
```
GET /api/tags
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "Vue.js",
      "slug": "vuejs",
      "articleCount": 5,
      "createdAt": "2024-12-01T10:00:00",
      "updatedAt": "2024-12-01T10:00:00"
    }
  ]
}
```

### 2. 获取单个标签

**请求**
```
GET /api/tags/{id}
```

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Vue.js",
    "slug": "vuejs",
    "articleCount": 5,
    "createdAt": "2024-12-01T10:00:00",
    "updatedAt": "2024-12-01T10:00:00"
  }
}
```

### 3. 创建标签

**请求**
```
POST /api/tags
Authorization: {token}
Content-Type: application/json

{
  "name": "Vue.js",
  "slug": "vuejs"
}
```

**说明**
- 需要管理员权限（@SaCheckRole("admin")）
- `name`: 必填，标签名称，最多50个字符，不能重复
- `slug`: 可选，标签别名，只能包含小写字母、数字和连字符，不能重复。如果不提供，系统会自动生成

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Vue.js",
    "slug": "vuejs",
    "articleCount": 0,
    "createdAt": "2024-12-01T10:00:00",
    "updatedAt": "2024-12-01T10:00:00"
  }
}
```

### 4. 更新标签

**请求**
```
PUT /api/tags/{id}
Authorization: {token}
Content-Type: application/json

{
  "name": "Vue.js 3",
  "slug": "vuejs3"
}
```

**说明**
- 需要管理员权限（@SaCheckRole("admin")）
- 参数要求同创建标签

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Vue.js 3",
    "slug": "vuejs3",
    "articleCount": 5,
    "createdAt": "2024-12-01T10:00:00",
    "updatedAt": "2024-12-20T15:30:00"
  }
}
```

### 5. 删除标签

**请求**
```
DELETE /api/tags/{id}
Authorization: {token}
```

**说明**
- 需要管理员权限（@SaCheckRole("admin")）
- 只能删除没有关联文章的标签（articleCount = 0）

**响应**
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 错误响应

### 参数错误（400）
```json
{
  "code": 400,
  "message": "标签名称不能为空"
}
```

### 未授权（401）
```json
{
  "code": 401,
  "message": "未登录或登录已过期"
}
```

### 权限不足（403）
```json
{
  "code": 403,
  "message": "无此角色权限"
}
```

### 业务错误（500）
```json
{
  "code": 500,
  "message": "标签名称已存在"
}
```

## 验证规则

### 标签名称（name）
- 必填
- 长度：1-50个字符
- 唯一性：不能与已有标签重复

### 标签别名（slug）
- 可选（不提供时自动生成）
- 格式：只能包含小写字母、数字和连字符（a-z, 0-9, -）
- 长度：1-50个字符
- 唯一性：不能与已有标签重复

### 删除限制
- 只能删除 `articleCount = 0` 的标签
- 如果标签下有文章，会返回错误信息："该标签下还有 X 篇文章，无法删除"

## 前端使用示例

```typescript
import { getTags, createTag, updateTag, deleteTag } from '@/api/tag'

// 获取所有标签
const loadTags = async () => {
  const response = await getTags()
  console.log(response.data) // 标签数组
}

// 创建标签
const createNewTag = async () => {
  const response = await createTag({
    name: 'Vue.js',
    slug: 'vuejs'
  })
  console.log(response.data) // 新创建的标签
}

// 更新标签
const updateExistingTag = async (id: number) => {
  const response = await updateTag(id, {
    name: 'Vue.js 3',
    slug: 'vuejs3'
  })
  console.log(response.data) // 更新后的标签
}

// 删除标签
const deleteExistingTag = async (id: number) => {
  await deleteTag(id)
  console.log('删除成功')
}
```

## 数据库表结构

```sql
CREATE TABLE IF NOT EXISTS tags (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    slug VARCHAR(50) NOT NULL UNIQUE COMMENT '标签别名（URL友好）',
    article_count INT UNSIGNED DEFAULT 0 COMMENT '文章数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';
```

## 注意事项

1. **权限控制**：创建、更新、删除操作需要管理员权限
2. **唯一性**：标签名称和别名都必须唯一
3. **删除限制**：只能删除没有关联文章的标签
4. **自动生成slug**：如果不提供slug，系统会根据name自动生成
5. **事务处理**：创建、更新、删除操作都使用了事务，确保数据一致性
