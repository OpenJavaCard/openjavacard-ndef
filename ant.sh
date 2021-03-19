#!/bin/sh
export PATH=/usr/lib/jvm/oracle-java8-jdk-amd64/bin:$PATH
export JAVA_HOME=/usr/lib/jvm/oracle-java8-jdk-amd64
export JAVACARD_HOME=/home/user/Exports/javacard-sdk/jc222_kit/
ant -DJAVACARD_HOME=$JAVACARD_HOME $@
