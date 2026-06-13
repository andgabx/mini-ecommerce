FROM gradle:9.1.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle installDist --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/install/products-service/ .
EXPOSE 5002
ENTRYPOINT ["./bin/products-service"]
