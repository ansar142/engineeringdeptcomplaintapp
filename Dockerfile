FROM openjdk:21-jdk
ADD target/engdept-app.jar engdept-app.jar
ENTRYPOINT ["java","-jar","/engdept-app.jar"]
