FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S zefflix && adduser -S zefflix -G zefflix
USER zefflix

COPY build/libs/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
