#!/bin/bash

echo "===== Phase 3: validate_service ====="
# 최대 대기 시간 (초) 및 대기 간격 설정
MAX_WAIT_TIME=60
SLEEP_INTERVAL=5
PORT=80
elapsed_time=0

# 서비스가 정상적으로 실행될 때까지 반복 확인 (nc 명령어 사용)
while [ "$elapsed_time" -lt "$MAX_WAIT_TIME" ]; do
    if nc -z localhost $PORT; then
        echo "Service is running successfully on port $PORT."
        exit 0
    fi
    echo "Waiting for service to start... ($elapsed_time/$MAX_WAIT_TIME seconds)"
    sleep $SLEEP_INTERVAL
    elapsed_time=$((elapsed_time + SLEEP_INTERVAL))
done

echo "Service failed to start on port $PORT within $MAX_WAIT_TIME seconds."
exit 1