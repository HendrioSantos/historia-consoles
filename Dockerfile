FROM maven:3.9.6-eclipse-temurin-21-alpine
WORKDIR /app
COPY . .
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"
CMD ["mvn", "spring-boot:run", "-Dmaven.resources.filtering=false"]