FROM eclipse-temurin:17.0.13_11-jdk AS builder
WORKDIR /opt/builder
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests


FROM eclipse-temurin:17.0.13_11-jre-alpine

ENV APP_NAME=hello-world
ENV JAR_FILE=${APP_NAME}-bin.jar
ENV APP_HOME=/opt/${APP_NAME}

WORKDIR ${APP_HOME}

ENV JAVA_OPTS="-XX:+PrintCommandLineFlags -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+8 -Dfile.encoding=UTF-8 -Djava.awt.headless=true"
ENV JAVA_OPTS="${JAVA_OPTS} -Xlog:gc*:file=${APP_HOME}/logs/gc.log:time,tags:filecount=10,filesize=100M"
ENV APP_OPTS="-Xms128m -Xmx1g"

RUN mkdir -p ${APP_HOME}/logs/

COPY --from=builder /opt/builder/wo-springboot-demo/target/${JAR_FILE} ${APP_HOME}

ENTRYPOINT [ "/bin/sh", "-c", "java ${JAVA_OPTS} ${APP_OPTS} -jar ${APP_HOME}/${JAR_FILE} ${0} ${@}" ]
