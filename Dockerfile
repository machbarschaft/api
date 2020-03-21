FROM adoptopenjdk/openjdk11:slim
VOLUME /tmp
COPY /build/libs/colivery-api-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
