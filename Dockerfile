FROM openjdk:17-jdk-slim
EXPOSE 8080:8080
ADD target/aws-redis-lock.jar aws-redis-lock.jar
ENTRYPOINT ["java","-jar","/aws-redis-lock.jar"]
