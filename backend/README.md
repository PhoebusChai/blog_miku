# 个人博客后端

基于 Spring Boot 3 + Sa-Token + MyBatis 构建的个人博客后端系统。

## 技术栈

- **Spring Boot 3.2.0** - 基础框架
- **Sa-Token 1.37.0** - 权限认证框架（集成 JWT）
- **MyBatis 3.0.3** - 持久层框架
- **MySQL 8.0+** - 数据库
- **Hutool 5.8.24** - Java 工具类库
- **Lombok** - 简化代码

## 项目结构

```
backend/
├── src/main/java/com/blog/
│   ├── BlogApplication.java          # 启动类
│   ├── common/                       # 通用类
│   │   ├── Result.java              # 统一响应结果
│   │   └── PageResult.java          # 分页结果
│   ├── config/                       # 配置类
│   │   ├── WebConfig.java           # Web 配置（CORS）
│   │   └── SaTokenConfig.java       # Sa-Token 配置
│   ├── controller/                   # 控制器
│   │   ├── AuthController.java      # 认证接口
│   │   └── ArticleController.java   # 文章接口
│   ├── dto/                          # 数据传输对象
│   │   ├── LoginDTO.java            # 登录参数
│   │   └── RegisterDTO.java         # 注册参数
│   ├── entity/                       # 实体类
│   │   ├── User.java                # 用户
│   │   ├── Article.java             # 文章
│   │   ├── Comment.java             # 评论
│   │   ├── Tag.java                 # 标签
│   │   ├── Project.java             # 项目
│   │   └── FriendLink.java          # 友链
│   ├── mapper/                       # MyBatis Mapper
│   │   ├── UserMapper.java
│   │   └── ArticleMapper.java
│   ├── service/                      # 业务逻辑
│   │   ├── UserService.java
│   │   └── ArticleService.java
│   └── exception/                    # 异常处理
│       └── GlobalExceptionHandler.java
└── src/main/resources/
    ├── application.yml               # 配置文件
    └── mapper/                       # MyBatis XML（可选）

```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

执行 `database/schema.sql` 创建数据库和表。

修改 `application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personal_blog
    username: root
    password: your_password
```

### 3. 启动项目

```bash
# 使用 Maven
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/personal-blog-1.0.0.jar
```

访问：http://localhost:8080/api

## API 接口

### 认证接口

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 退出登录
- `GET /api/auth/current` - 获取当前用户信息

### 文章接口

- `GET /api/articles` - 获取文章列表（分页）
- `GET /api/articles/{id}` - 获取文章详情
- `POST /api/articles` - 创建文章（需登录）
- `PUT /api/articles/{id}` - 更新文章（需登录）
- `DELETE /api/articles/{id}` - 删除文章（需登录）

## Sa-Token 配置说明

Sa-Token 是一个轻量级的权限认证框架，本项目集成了 JWT 模式。

### 主要特性

- **登录认证**：`StpUtil.login(userId)` 登录，`StpUtil.logout()` 退出
- **权限校验**：`StpUtil.checkLogin()` 检查登录，`StpUtil.checkRole("admin")` 检查角色
- **Token 管理**：自动生成和验证 JWT Token
- **会话管理**：支持单点登录、多端登录等

### 使用示例

```java
// 登录
StpUtil.login(userId);

// 获取 Token
String token = StpUtil.getTokenValue();

// 检查登录
StpUtil.checkLogin();

// 获取当前用户 ID
int userId = StpUtil.getLoginIdAsInt();

// 退出登录
StpUtil.logout();
```

## 开发说明

### 添加新的实体和接口

1. 在 `entity` 包创建实体类
2. 在 `mapper` 包创建 Mapper 接口
3. 在 `service` 包创建 Service 类
4. 在 `controller` 包创建 Controller 类

### 权限控制

使用 Sa-Token 注解或工具类：

```java
// 需要登录
@SaCheckLogin
public void method() {}

// 需要角色
@SaCheckRole("admin")
public void method() {}

// 或使用工具类
StpUtil.checkLogin();
StpUtil.checkRole("admin");
```

## 注意事项

1. 默认管理员账号：admin@example.com / 123456
2. 密码使用 BCrypt 加密存储
3. Token 有效期默认 30 天
4. 开发阶段未使用 Spring Security，使用 Sa-Token 进行权限管理

## 后续扩展

可以继续添加以下功能：

- 评论管理接口
- 标签管理接口
- 项目管理接口
- 友链管理接口
- 图库管理接口
- 文件上传接口
- 网站配置接口
- 统计分析接口
