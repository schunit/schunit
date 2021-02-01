#!/bin/sh

BASEDIR=$(dirname $(dirname $(realpath "$0")))

JAVA_OPTS="${JAVA_OPTS:-} --add-opens java.base/java.lang=ALL-UNNAMED"

if [ "${JAVA_HOME:-}" = "" ]; then
    JAVAEXEC="java"
else
    JAVAEXEC="${JAVA_HOME}/bin/java"
fi

exec ${JAVAEXEC} ${JAVA_OPTS} -classpath "$(pwd):${BASEDIR}/lib/*" com.schunit.cli.Main "$@"