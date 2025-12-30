#!/bin/bash

# SSL 证书初始化脚本
# 使用 Let's Encrypt 免费证书

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 配置
DOMAIN=${1:-"example.com"}
EMAIL=${2:-"admin@example.com"}
STAGING=${3:-0}  # 设为 1 使用测试环境

echo "=========================================="
echo "  Let's Encrypt SSL 证书初始化"
echo "=========================================="
echo ""
echo "域名: $DOMAIN"
echo "邮箱: $EMAIL"
echo ""

# 检查参数
if [ "$DOMAIN" = "example.com" ]; then
    echo -e "${RED}错误: 请提供域名参数${NC}"
    echo "用法: ./init-ssl.sh your-domain.com your-email@example.com"
    exit 1
fi

# 创建必要目录
echo "创建目录..."
mkdir -p docker/certbot/conf
mkdir -p docker/certbot/www
mkdir -p docker/nginx/ssl/live

# 下载推荐的 TLS 参数
if [ ! -f "docker/certbot/conf/options-ssl-nginx.conf" ]; then
    echo "下载 SSL 配置..."
    curl -s https://raw.githubusercontent.com/certbot/certbot/master/certbot-nginx/certbot_nginx/_internal/tls_configs/options-ssl-nginx.conf > docker/certbot/conf/options-ssl-nginx.conf
fi

if [ ! -f "docker/certbot/conf/ssl-dhparams.pem" ]; then
    echo "下载 DH 参数..."
    curl -s https://raw.githubusercontent.com/certbot/certbot/master/certbot/certbot/ssl-dhparams.pem > docker/certbot/conf/ssl-dhparams.pem
fi

# 创建临时自签名证书（用于首次启动 Nginx）
if [ ! -f "docker/nginx/ssl/live/fullchain.pem" ]; then
    echo "创建临时自签名证书..."
    openssl req -x509 -nodes -newkey rsa:4096 -days 1 \
        -keyout docker/nginx/ssl/live/privkey.pem \
        -out docker/nginx/ssl/live/fullchain.pem \
        -subj "/CN=localhost"
fi

# 启动 Nginx
echo "启动 Nginx..."
docker compose up -d nginx

# 等待 Nginx 启动
sleep 5

# 删除临时证书
rm -f docker/nginx/ssl/live/fullchain.pem
rm -f docker/nginx/ssl/live/privkey.pem

# 申请证书
echo "申请 Let's Encrypt 证书..."

STAGING_ARG=""
if [ "$STAGING" = "1" ]; then
    STAGING_ARG="--staging"
    echo -e "${YELLOW}使用测试环境${NC}"
fi

docker compose run --rm certbot certonly --webroot \
    --webroot-path=/var/www/certbot \
    --email $EMAIL \
    --agree-tos \
    --no-eff-email \
    -d $DOMAIN \
    -d www.$DOMAIN \
    $STAGING_ARG

# 复制证书到 Nginx SSL 目录
echo "复制证书..."
cp docker/certbot/conf/live/$DOMAIN/fullchain.pem docker/nginx/ssl/live/fullchain.pem
cp docker/certbot/conf/live/$DOMAIN/privkey.pem docker/nginx/ssl/live/privkey.pem

# 重启 Nginx 加载新证书
echo "重启 Nginx..."
docker compose restart nginx

echo ""
echo -e "${GREEN}✓ SSL 证书配置完成！${NC}"
echo ""
echo "证书将自动续订（每12小时检查一次）"
echo "手动续订: docker compose run --rm certbot renew"
