FROM openjdk:21-slim
LABEL authors="sanghoon"

WORKDIR /app

# 2. 소스 코드를 컨테이너에 복사
COPY . .

# 3. Gradle 빌드를 수행
RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test --no-daemon

ENTRYPOINT ["java", "-jar", "/app/build/libs/mos.jar"]