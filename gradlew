#!/bin/sh
# Minimal gradlew for ZIP convenience
export GRADLE_OPTS="-Xmx1024m -Dfile.encoding=UTF-8"
APP_HOME=$( cd "$( dirname "$0" )" && pwd )
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
if [ ! -f "$CLASSPATH" ]; then
    echo "Error: gradle-wrapper.jar not found."
    echo "Please open this project in Android Studio to auto-generate it."
    exit 1
fi
java $GRADLE_OPTS -jar "$CLASSPATH" "$@"