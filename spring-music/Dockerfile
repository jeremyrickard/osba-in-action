FROM openjdk:8-jre

ADD build/libs/spring-music.jar .
ENV RUNTIME k8s
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "spring-music.jar"]
