FROM eclipse-temurin:17.0.13_11-jdk AS builder
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests


FROM eclipse-temurin:17.0.13_11-jre-alpine

ENV APP_NAME=hello-world
ENV JAR_FILE=${APP_NAME}-bin.jar
ENV APP_DIR=/app/${APP_NAME}

WORKDIR ${APP_DIR}

COPY --from=builder /app/wo-springboot-web/target/${JAR_FILE} ${APP_HOME}

RUN mkdir -p ${APP_DIR}/logs/

ENV JAVA_OPTS=" "
ENV JAVA_OPTS=" ${JAVA_OPTS} -XX:+PrintCommandLineFlags -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError "
ENV JAVA_OPTS=" ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+8 -Dfile.encoding=UTF-8 -Djava.awt.headless=true "
ENV JAVA_OPTS=" ${JAVA_OPTS} -XX:+UnlockExperimentalVMOptions -XX:ShenandoahUncommitDelay=60000 -XX:ZUncommitDelay=30000 -Xms64m -Xmx2g "
ENV JAVA_OPTS=" ${JAVA_OPTS} -Djava.io.tmpdir=${APP_DIR}/tmp "
ENV JAVA_OPTS=" ${JAVA_OPTS} -Xlog:gc*:file=${APP_DIR}/logs/gc.log:time,tags:filecount=10,filesize=100M "

ENV APP_OPTS=" "

ENTRYPOINT [ "/bin/sh", "-c", "java ${JAVA_OPTS} ${APP_OPTS} -jar ${APP_DIR}/${APP_FILE} ${0} ${@}" ]
