FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 配置 Maven 阿里云镜像
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>\
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">\
<mirrors>\
<mirror>\
<id>aliyun</id>\
<mirrorOf>central</mirrorOf>\
<name>Aliyun Maven Mirror</name>\
<url>https://maven.aliyun.com/repository/public</url>\
</mirror>\
</mirrors>\
</settings>' > /root/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 配置 Alpine 国内镜像 + 安装 curl 用于健康检查
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
    apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

COPY --from=builder /build/target/*.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads /app/logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.config.additional-location=file:/app/config/", "app.jar"]
