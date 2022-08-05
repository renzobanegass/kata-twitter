## BUILDER
FROM gradle:6.9.1-jdk8 AS builder
USER root
COPY src src
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY dependencies.gradle.kts dependencies.gradle.kts
RUN gradle test installDist

# RUNNER
FROM openjdk:8u212-jdk-alpine
WORKDIR /home/api
EXPOSE 8080
COPY --from=builder /home/gradle/build/install/kata-twitter-renzo  /home/api
RUN chmod +x /home/api/bin/kata-twitter-renzo

ENTRYPOINT ["/home/api/bin/kata-twitter-renzo"]
