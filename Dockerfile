#stage build
FROM maven:3.6.3-jdk-8 AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -Dmaven.test.skip=true

#stage deploy
FROM openjdk:8-jre
COPY --from=builder /app/essentialprogramming-api/target/lighthouse-automation.jar /app/lighthouse-automation.jar

EXPOSE 8082

CMD java -Xms512m -Xmx1024m -jar ./app/lighthouse-automation.jar