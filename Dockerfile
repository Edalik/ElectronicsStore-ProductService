FROM amazoncorretto:17.0.14-alpine
EXPOSE 8080
COPY ./build/libs/*.jar product-service.jar
ENTRYPOINT ["java", "-jar", "/product-service.jar"]