FROM openjdk:11.0-jdk-slim

ENV DB_URL=jdbc:postgresql://localhost:5432/calendar
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres

COPY ./ ./
RUN ./gradlew build -x test
COPY src/main/resources/application.yml /application.yml

CMD ["java", "-jar", "./build/libs/joom-test-0.0.1-SNAPSHOT.jar", "--spring.config.location=application.yml"]
