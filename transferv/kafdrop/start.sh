#!/bin/bash

docker run -it --rm -p 9000:9000 --network="transferv" -e KAFKA_BROKERCONNECT=broker:29092 -e SCHEMAREGISTRY_CONNECT="http://schema-registry:8081" obsidiandynamics/kafdrop