# Docker 部署说明

## 目录结构

```
docker/
├── mysql/
│   ├── data/          # MySQL 数据持久化
│   └── conf/          # MySQL 配置文件
├── redis/
│   ├── data/          # Redis 数据持久化
│   └── conf/          # Redis 配置文件
├── nginx/
│   ├── html/          # 前端构建产物
│   ├── conf/          # Nginx 主配置
│   ├── conf.d/        # Nginx 站点配置
│   ├── ssl/           # SSL 证书
│   └── logs/          # Nginx 日志
├── uploads/           # 上传文件存储
└── backend/
    └── logs/          # 后端日志
```

## 快速开始

### Windows

```batch
# 初始化并构建
deploy.bat build

# 启动服务
deploy.bat start

# 停止服务
deploy.bat stop

# 查看日志
deploy.bat logs
```

### Linux/Mac

```bash
# 添加执行权限
chmod +x deploy.sh

# 初始化并构建
./deploy.sh build

# 启动服务
./deploy.sh start

# 停止服务
./deploy.sh stop

# 查看日志
./deploy.sh logs
```

## 配置说明

### 环境变量

复制 `.env.example` 为 `.env` 并修改配置：

```bash
# MySQL 密码
MYSQL_ROOT_PASSWORD=your-password

# 邮件配置
MAIL_USERNAME=your-email@qq.com
MAIL_PASSWORD=your-smtp-password

# JWT 密钥
JWT_SECRET=your-secret-key
```

### 后端配置

编辑 `config/backend/application-prod.yml` 修改生产环境配置。

### Nginx 配置

- 主配置: `docker/nginx/conf/nginx.conf`
- 站点配置: `docker/nginx/conf.d/default.conf`

### HTTPS 配置

1. 将 SSL 证书放入 `docker/nginx/ssl/` 目录
2. 修改 `docker/nginx/conf.d/default.conf` 添加 HTTPS 配置

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx | 80/443 | 前端 + 反向代理 |
| Backend | 8080 | 后端 API |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |

## 数据备份

### MySQL 备份

```bash
docker exec blog-mysql mysqldump -u root -p personal_blog > backup.sql
```

### MySQL 恢复

```bash
docker exec -i blog-mysql mysql -u root -p personal_blog < backup.sql
```

## 常见问题

### 1. 后端启动失败

检查 MySQL 是否完全启动：
```bash
docker logs blog-mysql
```

### 2. 前端页面空白

确保前端已构建并复制到 `docker/nginx/html/`：
```bash
deploy.bat frontend  # Windows
./deploy.sh frontend # Linux/Mac
```

### 3. 上传文件无法访问

检查 `docker/uploads/` 目录权限和 Nginx 配置。
