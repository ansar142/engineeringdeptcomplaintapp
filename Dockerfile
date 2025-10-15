FROM amazoncorretto:21-alpine-jdk
ADD target/engdept-app.jar engdept-app.jar
ENTRYPOINT ["java","-jar","/engdept-app.jar"]
