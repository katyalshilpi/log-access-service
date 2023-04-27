FROM openjdk:11

COPY /target/log-access-service.jar /log-access-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Xmx500m", "-jar", "/log-access-service.jar"]
