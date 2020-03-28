FROM adoptopenjdk/openjdk11:slim
VOLUME /tmp
COPY build/libs/api-*.jar app.jar
ENTRYPOINT java -jar /app.jar --server.port=$PORT
