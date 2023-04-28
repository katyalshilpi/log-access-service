FROM openjdk:11

VOLUME /tmp
ARG JAR_FILE=target/log-access-service.jar
COPY ${JAR_FILE} log-access-service.jar
EXPOSE 18080
ENTRYPOINT ["java", "-Xmx500m", "-jar", "/log-access-service.jar"]


