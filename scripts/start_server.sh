#!/bin/bash

# 새로운 컨테이너를 백그라운드에서 실행
docker run -d -p 80:8080 --name mos shoon95/mos:latest