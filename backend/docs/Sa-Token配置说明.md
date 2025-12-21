# Sa-Token 配置说明

## 拦截器配置

### 当前配置

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
            .addPathPatterns("/**")
            .excludePathPatterns(
                    "/auth/**",           // 认证相关接口
                    "/email/**",          // 邮件相关接口
                    "/articles",          // 文章列表（公开）
                    "/articles/*",        // 文章详情（公开）
                    "/articles/*/comments", // 文章评论（公开）
                    "/tags",              // 标签列表（公开）
                    "/tags/*",            // 标签详情（公开）
                    "/uploads/**"         // 静态资源访问
            );
}
```

### 路径说明

#### 需要登录的接口
- `/upload/**` - 文件上传（需要登录）
- `/articles/my` - 我的文章（需要登录）
- `/comments/**` - 评论管理（需要登录）
- 其他未排除的接口

#### 公开访问的接口
- `/auth/**` - 登录、注册、登出
- `/email/**` - 邮件验证码
- `/articles` - 文章列表
- `/articles/{id}` - 文章详情
- `/articles/{id}/comments` - 文章评论列表
- `/tags` - 标签列表
- `/tags/{id}` - 标签详情
- `/uploads/**` - 静态资源（图片等）

## 注意事项

### 1. 路径匹配规则

- `/**` - 匹配所有路径
- `/*` - 只匹配一级路径
- `/path/**` - 匹配 /path 及其所有子路径

### 2. 上传接口安全

文件上传接口 `/upload/**` 需要登录才能访问，防止匿名用户上传文件。

### 3. 静态资源访问

`/uploads/**` 路径用于访问已上传的静态资源（图片等），无需登录即可访问。

### 4. 路径顺序

排除路径的顺序很重要，更具体的路径应该放在前面。

## 常见问题

### Q: 为什么上传接口需要登录？

A: 防止匿名用户恶意上传大量文件，占用服务器资源。

### Q: 如何添加新的公开接口？

A: 在 `excludePathPatterns` 中添加对应的路径模式。

### Q: 如何测试接口是否需要登录？

A: 使用 Postman 或浏览器，不带 token 访问接口，看是否返回 401 未授权。

## 扩展配置

### 角色权限控制

如果需要更细粒度的权限控制，可以使用：

```java
// 检查角色
registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkRole("admin")))
        .addPathPatterns("/admin/**");

// 检查权限
registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkPermission("article:delete")))
        .addPathPatterns("/articles/*/delete");
```

### 自定义拦截逻辑

```java
registry.addInterceptor(new SaInterceptor(handle -> {
    // 自定义逻辑
    if (某个条件) {
        StpUtil.checkLogin();
    }
})).addPathPatterns("/**");
```
