# OpenJDK 기반 이미지를 사용하여 Spring Boot 애플리케이션 빌드
FROM openjdk:11 AS build-spring

# 타임존 설정
ENV TZ=Asia/Seoul

WORKDIR /home/app

# java 디렉토리 생성
RUN mkdir -p /home/app
RUN mkdir -p /home/app/logs

COPY build/libs/ems-0.0.1-SNAPSHOT.jar /home/app

# 설치 및 설정을 위해 vi 편집기와 net-tools 패키지 설치
#RUN apt-get update && apt-get install -y vim net-tools git

# Spring Boot 애플리케이션 실행 포트
EXPOSE 10014

# Spring Boot 애플리케이션 실행
CMD ["java", "-jar", "/home/app/ems-0.0.1-SNAPSHOT.jar"]