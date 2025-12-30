#!/bin/bash

# SSL 证书续订脚本
# 可添加到 crontab 定期执行

set -e

DOMAIN=${1:-""}

echo "=========================================="
echo "  Let's Encrypt SSL 证书续订"
echo "=========================================="

# 续订证书
docker compose run --rm certbot renew

# 如果提供了域名，复制证书
if [ -n "$DOMAIN" ] && [ -d "docker/certbot/conf/live/$DOMAIN" ]; then
    echo "复制证书..."
    cp docker/certbot/conf/live/$DOMAIN/fullchain.pem docker/nginx/ssl/live/fullchain.pem
    cp docker/certbot/conf/live/$DOMAIN/privkey.pem docker/nginx/ssl/live/privkey.pem
fi

# 重载 Nginx
echo "重载 Nginx..."
docker compose exec nginx nginx -s reload

echo "✓ 证书续订完成"
