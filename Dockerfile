FROM amazoncorretto:21
COPY target/trading-1.0.0.jar trading-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/trading-1.0.0.jar"]
