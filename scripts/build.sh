#!/bin/bash

echo "开始编译"

#编译项目
cd ..
mvn clean install
cd tracer-agent
mvn assembly:assembly
cd target
zip -r -o tracer-agent.zip tracer-agent.jar
cd ../../
cp tracer-agent/target/tracer-agent.zip tracer-attach/src/main/resources
cd tracer-attach
mvn assembly:assembly
cd target
zip -r -o tracer-attach.zip tracer-attach.jar
cd ../../
cp tracer-attach/target/tracer-attach.zip tracer-launcher/src/main/resources
cd tracer-common
mvn clean install
cd ..
cd tracer-launcher
mvn clean install
cd ..
cd tracer-sping-boot-starter
mvn clean install

mvn clean install deploy

