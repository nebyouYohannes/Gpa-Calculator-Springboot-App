# GPA Calculator Spring Boot App Using RDF Storage

## Overview

This is a GPA Calculator application built using Spring Boot. The app allows users to calculate their Grade Point Average (GPA) based on their course grades and credits. It features a simple and intuitive interface for inputting grades and credits and provides real-time GPA calculations.

## Features

- **Grade Input:** Enter course names, grades, and corresponding credits.
- **GPA Calculation:** Automatically calculates and displays the GPA based on the input data.
- **Error Handling:** Provides user feedback for invalid inputs.
- **User-Friendly Interface:** Simple and clean web interface for easy use.

## Installation Instructions

### Prerequisites

- **Java 11 or newer:** Ensure that you have Java Development Kit (JDK) version 11 or higher installed.
- **Maven:** Used for dependency management and building the project.

### Clone the Repository

To clone the repository, run:

```bash
git clone https://github.com/nebyouYohannes/Gpa-Calculator-Springboot-App.git
cd Gpa-Calculator-Springboot-App
``` 
### Build the Application

Navigate to the project directory and build the application using Maven:

```bash
mvn clean install
```
- **mvn clean:** Cleans the project by deleting the target directory.
- **mvn install:** Compiles the code, runs tests, and packages the application into a JAR file.

### Run the Application

After building the application, you can run it with:

```bash
mvn spring-boot:run
```

Alternatively, you can run the JAR file directly:

```bash
java -jar target/gpa-calculator-springboot-app.jar
```

The application will start running on http://localhost:8080 by default.
