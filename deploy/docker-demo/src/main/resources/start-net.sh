#!/bin/bash
echo "停止network"
docker-compose -f docker-compose-all.yaml down
echo "启动jdchain"
docker-compose -f docker-compose-all.yaml up -d
