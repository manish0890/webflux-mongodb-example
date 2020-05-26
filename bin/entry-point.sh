#!/bin/bash

echo "-------------------"
echo "Java_Head_Max_Memory: ${JAVA_HEAP_MAX}"
echo "JVM Garbage Collector: ${GARBAGE_COLLECTOR}"
echo "-------------------"

exec java ${JAVA_HEAP_MAX} ${GARBAGE_COLLECTOR} -jar ./target/skyline-service-*.jar