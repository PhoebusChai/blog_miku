# 构建阶段
FROM node:18-alpine AS builder

# 配置 Alpine 国内镜像
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

WORKDIR /app

# 复制 package 文件
COPY package*.json ./

# 安装依赖（使用淘宝镜像）
RUN npm ci --registry=https://registry.npmmirror.com

# 复制源代码
COPY . .

# 构建
RUN npm run build

# 输出阶段 - 仅用于提取构建产物
FROM alpine:latest

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

WORKDIR /dist

COPY --from=builder /app/dist .

CMD ["echo", "Frontend build complete. Copy dist folder to nginx html directory."]
