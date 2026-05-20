FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /opt/app

COPY src ./src
COPY pom.xml ./

RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests
RUN jar xf target/chupinvet-0.0.1-SNAPSHOT.jar
RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 21 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    target/chupinvet-0.0.1-SNAPSHOT.jar > deps.info

RUN jlink \
    --verbose \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /customjre

FROM gcr.io/distroless/base-debian12:nonroot

WORKDIR /app

COPY --from=build /customjre /opt/jre
COPY --from=build /opt/app/target/chupinvet-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_HOME=/opt/jre
ENV PATH="/opt/jre/bin:${PATH}"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]