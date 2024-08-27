@echo off

echo Building the project with Maven...
mvn clean install && (
    echo Running the Spring web application...
    java -jar target/gpaCalculator-0.0.1-SNAPSHOT.jar
)
