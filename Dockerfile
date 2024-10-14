FROM eclipse-temurin:21-jdk-alpine
COPY target/find-your-doctor-1.0-SNAPSHOT.jar find-your-doctor.jar
ENTRYPOINT ["java","-jar","/find-your-doctor.jar"]