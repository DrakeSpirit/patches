FROM anapsix/alpine-java:8
WORKDIR /patches
COPY build/libs .
COPY config.json .
CMD ["java", "-jar", "patches-1.0-SNAPSHOT.jar"]