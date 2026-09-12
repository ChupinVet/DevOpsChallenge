FROM maven:3-eclipse-temurin-25-alpine AS build

WORKDIR /opt/app

COPY pom.xml ./
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests

RUN jar xf target/*.jar && \
    jdeps --ignore-missing-deps -q \
      --recursive \
      --multi-release 25 \
      --print-module-deps \
      --class-path 'BOOT-INF/lib/*' \
      target/*.jar > deps.info && \
    jlink \
      --add-modules "$(cat deps.info)" \
      --strip-debug \
      --compress 2 \
      --no-header-files \
      --no-man-pages \
      --output /customjre

FROM alpine:3.21

RUN addgroup -S chupingroup && adduser -S chupinuser -G chupingroup

COPY --from=build /customjre /opt/jre-minimal

WORKDIR /app

ENV JAVA_HOME=/opt/jre-minimal \
    PATH="/opt/jre-minimal/bin:$PATH"

COPY --from=build \
     --chown=chupinuser:chupingroup \
     /opt/app/target/*.jar \
     api.jar

EXPOSE 8080

USER chupinuser

ENTRYPOINT ["java", "-jar", "api.jar"]