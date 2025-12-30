# 个人博客

一个简洁优雅的个人博客项目。

## 项目简介

这是一个现代化的个人博客系统，用于分享技术文章、生活感悟和个人作品。

## 功能特性

- 📝 文章发布与管理
- 🏷️ 标签分类系统
- 🔍 文章搜索功能
- 💬 评论系统
- 📱 响应式设计，支持移动端
- 🌙 深色模式支持
- ⚡ 快速加载，性能优化
- 🖼️ 图片上传与管理
- 🔗 友情链接管理
- 📊 项目展示
- 📧 邮件通知

## 技术栈

### 前端
- Vue 3 - 渐进式 JavaScript 框架
- TypeScript - 类型安全
- Element Plus - UI 组件库
- Vite - 下一代前端构建工具
- Lucide - 精美的图标库

### 后端
- Spring Boot 3.2 - Java 企业级应用框架
- MyBatis - 数据持久化框架
- Sa-Token - 权限认证框架
- MySQL 8.0 - 关系型数据库
- Redis - 缓存与会话存储

### 部署
- Docker & Docker Compose
- Nginx - 反向代理与静态资源服务

## 快速开始

### 环境要求

- Node.js 18+
- Java 17+
- Docker & Docker Compose
- MySQL 8.0（本地开发）
- Redis（本地开发）

### 本地开发

#### 前端

```bash
cd frontend
npm install
npm run dev
```

#### 后端

```bash
cd backend
mvn spring-boot:run
```

### Docker 部署

#### 1. 配置环境变量

```bash
# 复制环境变量示例文件
cp .env.example .env

# 编辑配置
# - MYSQL_ROOT_PASSWORD: MySQL 密码
# - MAIL_USERNAME: 邮箱账号
# - MAIL_PASSWORD: 邮箱 SMTP 密码
# - JWT_SECRET: JWT 密钥
```

#### 2. 一键部署

**Windows:**
```batch
deploy.bat build
```

**Linux/Mac:**
```bash
chmod +x deploy.sh
./deploy.sh build
```

#### 3. 访问服务

- 前端: http://localhost
- 后端 API: http://localhost/api

#### 4. 配置 HTTPS（生产环境）

使用 Let's Encrypt 免费 SSL 证书：

```bash
# Linux/Mac
chmod +x scripts/init-ssl.sh
./scripts/init-ssl.sh your-domain.com your-email@example.com

# 手动续订证书
./scripts/renew-ssl.sh your-domain.com
```

证书会自动每 12 小时检查续订。

### 部署命令

| 命令 | 说明 |
|------|------|
| `deploy.bat init` | 初始化环境 |
| `deploy.bat start` | 启动服务 |
| `deploy.bat stop` | 停止服务 |
| `deploy.bat restart` | 重启服务 |
| `deploy.bat logs` | 查看日志 |
| `deploy.bat build` | 构建并启动 |
| `deploy.bat frontend` | 仅构建前端 |

## 项目结构

```
personal-blog/
├── frontend/                # 前端项目 (Vue 3 + TypeScript)
│   ├── src/
│   │   ├── api/            # API 接口
│   │   ├── components/     # 组件
│   │   ├── views/          # 页面
│   │   ├── stores/         # 状态管理
│   │   └── utils/          # 工具函数
│   └── Dockerfile
├── backend/                 # 后端项目 (Spring Boot)
│   ├── src/main/java/com/blog/
│   │   ├── controller/     # 控制器
│   │   ├── service/        # 服务层
│   │   ├── mapper/         # 数据访问层
│   │   ├── entity/         # 实体类
│   │   └── config/         # 配置类
│   └── Dockerfile
├── database/                # 数据库脚本
│   └── schema.sql          # 建表语句
├── config/                  # 配置文件
│   └── backend/
│       └── application-prod.yml
├── docker/                  # Docker 数据卷
│   ├── mysql/              # MySQL 数据与配置
│   ├── redis/              # Redis 数据与配置
│   ├── nginx/              # Nginx 配置与前端文件
│   ├── uploads/            # 上传文件
│   └── backend/logs/       # 后端日志
├── docker-compose.yml       # Docker 编排文件
├── deploy.sh               # Linux 部署脚本
├── deploy.bat              # Windows 部署脚本
└── .env.example            # 环境变量示例
```

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx | 80 | 前端 + 反向代理 |
| Backend | 8080 | 后端 API |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |

## 数据持久化

所有数据挂载到 `docker/` 目录：

- `docker/mysql/data/` - MySQL 数据
- `docker/redis/data/` - Redis 数据
- `docker/uploads/` - 上传文件
- `docker/nginx/logs/` - Nginx 日志
- `docker/backend/logs/` - 后端日志

## 配置说明

### 后端配置

编辑 `config/backend/application-prod.yml`：

- 数据库连接
- Redis 配置
- 邮件服务配置
- JWT 密钥
- 文件上传路径

### Nginx 配置

- 主配置: `docker/nginx/conf/nginx.conf`
- 站点配置: `docker/nginx/conf.d/default.conf`

## 许可证

MIT License
