FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/chupinvet-0.0.1-SNAPSHOT.jar app.jar

RUN useradd -m springuser
USER springuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]