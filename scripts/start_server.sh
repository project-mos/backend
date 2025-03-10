#!/bin/bash

echo "===== Phase 2: start_server ====="
# Docker Compose로 전체 스택을 백그라운드에서 실행 (의존 서비스들은 Healthcheck를 통과해야 실행됨)
cd /home/ubuntu/deployment/scripts

docker-compose up -d