FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY . .

RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /work
COPY --from=build /workspace/target/quarkus-app/ /work/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]