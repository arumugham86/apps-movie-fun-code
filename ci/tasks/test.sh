#!/bin/bash

set -ex

pushd moviefun-service-source
  echo "Fetching Dependencies"
  ./mvnw clean compile > /dev/null

  echo "Running Tests"
  ./mvnw test
popd

exit 0
