FROM java:8
EXPOSE 8080 9001
ADD /target/SpringBootCamelStreamsExample-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","SpringBootCamelStreamsExample-0.0.1-SNAPSHOT.jar"]