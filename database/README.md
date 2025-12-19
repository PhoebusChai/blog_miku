# 数据库设计文档

## 概述

本数据库为个人博客系统设计，支持文章管理、用户系统、评论留言、项目展示、图库等功能。

## 数据库信息

- 数据库名称：`personal_blog`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`

## 表结构说明

### 1. users（用户表）

存储用户信息，支持管理员和普通用户两种角色。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 用户ID（主键） |
| email | VARCHAR(100) | 邮箱（唯一） |
| password | VARCHAR(255) | 密码（加密存储） |
| name | VARCHAR(50) | 用户名 |
| avatar | VARCHAR(500) | 头像URL |
| role | TINYINT UNSIGNED | 用户角色（0-普通用户，1-管理员） |
| status | TINYINT | 状态（1-正常，0-禁用） |

### 2. articles（文章表）

存储博客文章内容。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 文章ID（主键） |
| title | VARCHAR(200) | 文章标题 |
| summary | VARCHAR(500) | 文章摘要 |
| content | LONGTEXT | 文章内容（Markdown） |
| cover_image | VARCHAR(500) | 封面图片URL |
| author_id | INT UNSIGNED | 作者ID（外键） |
| view_count | INT UNSIGNED | 浏览次数 |
| like_count | INT UNSIGNED | 点赞次数 |
| comment_count | INT UNSIGNED | 评论次数 |
| status | TINYINT UNSIGNED | 状态（0-草稿，1-已发布，2-仅自己可见，3-已删除） |
| is_top | TINYINT | 是否置顶 |
| published_at | TIMESTAMP | 发布时间 |

### 3. tags（标签表）

存储文章标签。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 标签ID（主键） |
| name | VARCHAR(50) | 标签名称（唯一） |
| slug | VARCHAR(50) | 标签别名（URL友好） |
| article_count | INT UNSIGNED | 文章数量 |

### 4. article_tags（文章标签关联表）

多对多关系，关联文章和标签。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 关联ID（主键） |
| article_id | INT UNSIGNED | 文章ID（外键） |
| tag_id | INT UNSIGNED | 标签ID（外键） |

### 5. comments（评论表）

存储文章评论和留言板留言，支持嵌套回复。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 评论ID（主键） |
| article_id | INT UNSIGNED | 文章ID（NULL表示留言板） |
| user_id | INT UNSIGNED | 用户ID（NULL表示游客） |
| parent_id | INT UNSIGNED | 父评论ID（用于回复） |
| guest_name | VARCHAR(50) | 游客昵称 |
| guest_email | VARCHAR(100) | 游客邮箱 |
| content | TEXT | 评论内容 |
| ip_address | VARCHAR(45) | IP地址 |
| like_count | INT UNSIGNED | 点赞次数 |
| status | TINYINT UNSIGNED | 状态（0-待审核，1-已通过，2-已拒绝，3-垃圾评论） |

### 6. projects（项目表）

存储个人项目信息。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 项目ID（主键） |
| title | VARCHAR(100) | 项目名称 |
| description | TEXT | 项目描述 |
| cover_image | VARCHAR(500) | 项目封面 |
| demo_url | VARCHAR(200) | 演示地址 |
| github_url | VARCHAR(200) | GitHub地址 |
| tech_stack | JSON | 技术栈（JSON数组） |
| status | TINYINT UNSIGNED | 项目状态（0-进行中，1-已完成，2-已归档） |
| sort_order | INT | 排序顺序 |
| view_count | INT UNSIGNED | 浏览次数 |

### 7. gallery（图库表）

存储图片资源。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 图片ID（主键） |
| title | VARCHAR(100) | 图片标题 |
| description | TEXT | 图片描述 |
| image_url | VARCHAR(500) | 图片URL |
| thumbnail_url | VARCHAR(500) | 缩略图URL |
| category | VARCHAR(50) | 分类 |
| sort_order | INT | 排序顺序 |
| status | TINYINT UNSIGNED | 状态（0-隐藏，1-显示） |

### 8. friend_links（友情链接表）

存储友情链接。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 链接ID（主键） |
| name | VARCHAR(50) | 网站名称 |
| url | VARCHAR(200) | 网站地址 |
| avatar | VARCHAR(500) | 网站头像 |
| description | VARCHAR(200) | 网站描述 |
| status | TINYINT UNSIGNED | 审核状态（0-待审核，1-已通过，2-已拒绝） |
| sort_order | INT | 排序顺序 |

### 9. site_config（网站配置表）

存储网站全局配置。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 配置ID（主键） |
| config_key | VARCHAR(50) | 配置键（唯一） |
| config_value | TEXT | 配置值 |
| config_type | TINYINT UNSIGNED | 配置类型（0-字符串，1-数字，2-布尔值，3-JSON） |
| description | VARCHAR(200) | 配置描述 |

### 10. reading_history（阅读记录表）

记录登录用户的文章阅读历史（不记录游客数据）。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT UNSIGNED | 记录ID（主键） |
| user_id | INT UNSIGNED | 用户ID（外键，必填） |
| article_id | INT UNSIGNED | 文章ID（外键） |
| created_at | TIMESTAMP | 首次阅读时间 |
| updated_at | TIMESTAMP | 最后阅读时间 |

### 11. visit_logs（访问统计表）

记录网站访问日志。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT UNSIGNED | 日志ID（主键） |
| user_id | INT UNSIGNED | 用户ID |
| ip_address | VARCHAR(45) | IP地址 |
| user_agent | VARCHAR(500) | 用户代理 |
| referer | VARCHAR(500) | 来源页面 |
| path | VARCHAR(500) | 访问路径 |
| method | VARCHAR(10) | 请求方法 |
| status_code | INT | 响应状态码 |
| response_time | INT | 响应时间（毫秒） |

## 使用说明

### 1. 导入数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行 SQL 文件
source database/schema.sql
```

或者使用命令行直接导入：

```bash
mysql -u root -p < database/schema.sql
```

### 2. 默认账号

数据库已包含两个测试账号：

**管理员账号：**
- 邮箱：admin@example.com
- 密码：123456

**普通用户账号：**
- 邮箱：user@example.com
- 密码：123456

> 注意：密码字段存储的是加密后的值，实际使用时需要在后端使用 bcrypt 等加密算法处理。

### 3. 索引说明

- 所有主键自动创建聚簇索引
- 外键字段创建普通索引以提高查询性能
- 常用查询字段（如 email、status、created_at）创建索引
- 文章表的 title 和 content 字段创建全文索引，支持全文搜索

### 4. 数据完整性

- 使用外键约束保证数据一致性
- 级联删除：删除文章时自动删除相关评论和标签关联
- 设置为 NULL：删除用户时评论的 user_id 设置为 NULL（保留游客评论）

## 扩展建议

### 1. 性能优化

- 对于高并发场景，可以考虑使用 Redis 缓存热门文章
- 访问统计表数据量大时，可以考虑分表或使用时序数据库
- 定期归档历史访问日志

### 2. 功能扩展

- 添加文章分类表（categories）
- 添加文章收藏表（favorites）
- 添加用户关注表（follows）
- 添加消息通知表（notifications）
- 添加文章草稿历史表（article_revisions）

### 3. 安全建议

- 定期备份数据库
- 使用强密码策略
- 限制数据库用户权限
- 启用 SQL 审计日志
- 对敏感字段（如密码）进行加密存储

## ER 图

```
users (1) ----< (N) articles
users (1) ----< (N) comments
articles (N) ----< (N) tags (通过 article_tags)
articles (1) ----< (N) comments
comments (1) ----< (N) comments (自关联，用于回复)
```

## 版本历史

- v1.0.0 (2024-12-19) - 初始版本，包含基础功能表结构
