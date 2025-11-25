# Docker 镜像构建 for 订单管理系统
# @author Claude Code Assistant
# 参考官方推荐的多阶段构建方式，减少最终镜像体积

# 构建阶段
FROM maven:3.9.8-eclipse-temurin-17-alpine as builder

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY pom.xml .

# 复制源码
COPY src ./src

# 构建项目
RUN mvn clean package -DskipTests

# 最终的运行镜像使用轻量级 JDK
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 从构建阶段复制构建好的 jar 包
COPY --from=builder /app/target/order-manage-0.0.1-SNAPSHOT.jar app.jar

# 设置 JVM 参数，根据实际情况调整
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseStringDeduplication"

# 运行时切换为生产环境配置
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露端口（根据 application.yml 中的端口配置）
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 运行命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE"]