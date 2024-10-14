FROM eclipse-temurin:21-jdk-alpine
COPY target/medicament-outlet-1.0-SNAPSHOT.jar medicament-outlet.jar
ENTRYPOINT ["java","-jar","/medicament-outlet.jar"]