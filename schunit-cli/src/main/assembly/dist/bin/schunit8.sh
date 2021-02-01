#!/bin/sh

BASEDIR=$(dirname $(dirname $(realpath "$0")))

if [ "${JAVA_HOME:-}" = "" ]; then
    JAVAEXEC="java"
else
    JAVAEXEC="${JAVA_HOME}/bin/java"
fi

exec ${JAVAEXEC} ${JAVA_OPTS:-} -classpath "$(pwd):${BASEDIR}/lib/*" com.schunit.cli.Main "$@"