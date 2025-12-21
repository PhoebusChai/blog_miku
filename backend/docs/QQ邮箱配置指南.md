# QQ 邮箱配置指南

## 1. 开启 QQ 邮箱 SMTP 服务

### 步骤 1：登录 QQ 邮箱
访问 https://mail.qq.com/ 并登录你的 QQ 邮箱。

### 步骤 2：进入设置
点击顶部的 **设置** -> **账户**

### 步骤 3：开启 SMTP 服务
1. 找到 **POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务** 部分
2. 找到 **IMAP/SMTP服务** 或 **POP3/SMTP服务**
3. 点击 **开启** 按钮

### 步骤 4：获取授权码
1. 开启服务时，QQ 会要求你发送短信验证
2. 按照提示发送短信到指定号码
3. 验证成功后，QQ 会生成一个 **授权码**（16位字符）
4. **重要：请妥善保存这个授权码，它将用于应用程序登录**

## 2. 配置 application.yml

将获取到的授权码配置到 `application.yml` 文件中：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your-qq-email@qq.com  # 替换为你的QQ邮箱
    password: your-authorization-code  # 替换为你获取的授权码（不是QQ密码）
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
```

### 配置说明

- **host**: QQ 邮箱 SMTP 服务器地址，固定为 `smtp.qq.com`
- **port**: 端口号，使用 `587`（TLS）或 `465`（SSL）
- **username**: 你的完整 QQ 邮箱地址
- **password**: 授权码（不是 QQ 密码！）

## 3. 安装 Redis

验证码存储需要 Redis，请确保已安装并启动 Redis。

### Windows 安装
1. 下载 Redis：https://github.com/tporadowski/redis/releases
2. 解压后运行 `redis-server.exe`

### Linux/Mac 安装
```bash
# Ubuntu/Debian
sudo apt-get install redis-server

# CentOS/RHEL
sudo yum install redis

# Mac
brew install redis

# 启动 Redis
redis-server
```

### Docker 安装
```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

## 4. 测试邮件发送

启动项目后，可以通过以下接口测试：

```bash
# 发送验证码
curl -X POST "http://localhost:8080/api/email/send-code?email=test@example.com"
```

## 5. 常见问题

### Q1: 535 Login Fail 错误
**原因**：授权码错误或未开启 SMTP 服务
**解决**：
1. 确认已开启 QQ 邮箱的 SMTP 服务
2. 确认使用的是授权码，不是 QQ 密码
3. 重新生成授权码并配置

### Q2: 连接超时
**原因**：网络问题或端口被占用
**解决**：
1. 检查网络连接
2. 尝试使用 465 端口（SSL）
3. 检查防火墙设置

### Q3: Redis 连接失败
**原因**：Redis 未启动或配置错误
**解决**：
1. 确认 Redis 已启动：`redis-cli ping`（应返回 PONG）
2. 检查 Redis 配置是否正确
3. 如果 Redis 有密码，需要在配置中添加

### Q4: 发送频率限制
**原因**：QQ 邮箱有发送频率限制
**解决**：
1. 代码中已实现 5 分钟内只能发送一次的限制
2. 避免短时间内大量发送邮件
3. 如需大量发送，建议使用专业的邮件服务（如阿里云邮件推送）

## 6. 生产环境建议

### 使用专业邮件服务
对于生产环境，建议使用专业的邮件服务：
- 阿里云邮件推送
- 腾讯云邮件服务
- SendGrid
- AWS SES

### 配置邮件模板
可以使用 HTML 模板美化邮件内容：

```java
// 使用 MimeMessage 发送 HTML 邮件
MimeMessage message = mailSender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
helper.setFrom(from);
helper.setTo(email);
helper.setSubject("【个人博客】邮箱验证码");
helper.setText(htmlContent, true);  // true 表示 HTML 格式
mailSender.send(message);
```

### 异步发送
建议使用异步方式发送邮件，避免阻塞主线程：

```java
@Async
public void sendVerificationCodeAsync(String email) {
    // 发送邮件逻辑
}
```

## 7. API 接口说明

### 发送验证码
```
POST /api/email/send-code?email=xxx@qq.com
```

**响应示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 使用验证码注册
```
POST /api/auth/register-with-code
Content-Type: application/json

{
  "email": "test@qq.com",
  "code": "123456",
  "password": "123456",
  "name": "测试用户"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```
