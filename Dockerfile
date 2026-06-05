# 第二阶段：运行（使用完整的JDK 21基础镜像）
FROM eclipse-temurin:21-jdk

# 设置工作目录
WORKDIR /app

# 创建日志和dump文件存放目录
RUN mkdir -p /app/logs

# 从构建阶段复制jar包（如果你跳过第一阶段，直接COPY target/*.jar app.jar）
COPY --from=build /app/target/*.jar app.jar

# 设置JDK内部的环境变量，确保JVM感知容器内存限制
# 这是一个“防御性”设置，但在使用完整JDK时通常不是必须的，但保留无害
ENV JAVA_OPTS="\
    -Xms256m \
    -Xmx256m \
    -XX:+UseG1GC \
    -Xlog:gc*:file=/app/logs/gc.log:time,uptime:filecount=5,filesize=10M \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/logs/dump.hprof"

# 暴露应用端口（根据实际情况修改）
EXPOSE 8080

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]