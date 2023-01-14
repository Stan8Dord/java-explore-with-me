FROM amazoncorretto:11-alpine-jdk
COPY target/mainservice*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]