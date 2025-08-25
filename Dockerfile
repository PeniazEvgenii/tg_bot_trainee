FROM eclipse-temurin:21-jre-alpine

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} user.jar

ENTRYPOINT ["java","-Xms128m", "-Xmx256m","-jar","/user.jar"]