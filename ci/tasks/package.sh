#!/bin/bash

set -e +x

pushd moviefun-service-source
  echo "Packaging WAR"
  ./mvnw clean package -DskipTests -Dmaven.test.skip=true
popd

war_count=`find moviefun-service-source/target -type f -name *.war | wc -l`

if [ $war_count -gt 1 ]; then
  echo "More than one jar found, don't know which one to deploy. Exiting"
  exit 1
fi

find moviefun-service-source/target -type f -name *.war -exec cp "{}" package-output/moviefun-service.war \;

echo "Done packaging"
exit 0
