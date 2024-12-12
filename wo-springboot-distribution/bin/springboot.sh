#!/bin/sh
set -e

APP_DIR=$(cd $(dirname $0) && pwd)
APP_NAME=$(basename ${APP_DIR})
APP_FILE=${APP_DIR}/${APP_NAME}-bin.jar

#JAVA_HOME=${JAVA_HOME}
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif type -p java; then
    JAVA=java
else
    echo "Error: JAVA_HOME is not set and java could not be found in PATH." 1>&2
    exit 1
fi

# GC: -Xlog:gc*:file=${APP_DIR}/logs/gc.log:time,tags:filecount=10,filesize=100M
JAVA_OPTS="${JAVA_OPTS} -XX:+PrintCommandLineFlags -XX:+HeapDumpOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+8 -Dfile.encoding=UTF-8 -Djava.awt.headless=true "
# jvm memory
JAVA_OPTS="${JAVA_OPTS} -Xms128m -Xmx1g -Dspring.application.name=hello-world"

PID=""

check_pid() {
    PID=$(ps -ef | grep "spring.application.name=${APP_NAME}" | grep -v "grep" | awk '{print $2}')
}

status() {
    check_pid
    if [ -n "$PID" ]; then
        echo "Active: running"
        echo "PID: $PID"
    else
        echo "Active: stopped"
    fi
}

stop() {
    check_pid
    if [ -n "$PID" ]; then
        echo "stopping..."
        kill $PID
    fi
    wait_count="0"
    wait_step="1"
    while [ $wait_count -lt 60 ]; do
        check_pid
        if [ -z "$PID" ]; then
            break
        fi
        wait_count=$(expr $wait_count + $wait_step)
        sleep $wait_step
        echo -n "."
    done
    echo ""
    check_pid
    if [ -n "$PID" ]; then
        kill -9 $PID
        echo "stop by SIGKILL"
    fi
    status
}

start() {
    check_pid
    if [ -n "$PID" ]; then
        status
        exit 1
    fi
    echo "starting..."
    nohup "$JAVA" ${JAVA_OPTS} -jar "${APP_FILE}" >"${APP_DIR}/${APP_NAME}.out" 2>&1 </dev/null &
    status
}

restart() {
    stop
    sleep 1
    start
}

case "$1" in
start)
    start
    ;;
stop)
    stop
    ;;
status)
    status
    ;;
restart)
    restart
    ;;
*)
    echo "USAGE: $0 start|stop|status|restart"
    exit 1
    ;;
esac
