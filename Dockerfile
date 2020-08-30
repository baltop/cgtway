# docker build -t apim_gtway .
# docker run --rm -p 8081:8081 apim_gtway
FROM adoptopenjdk/openjdk11:alpine-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
