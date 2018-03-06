# Sample spring boot camel application


Goal of this project is a a proof of concept for a system that can keep track of balances from several sources.
In this repo, you'll find two files, Sheet.csv and Sheet.txt. These files can be uploaded or fetched via file system by apache camel
and displayed in a paginated table made in reactjs.


## Compiling and running the application

Requisites:

* Java8 JDK
* Lombok
* Maven
* Docker

The application can be run in 2 different ways:

* with a docker container for the database
* with a docker container for the database and the application

The preferred way for development/test is by using only the database container while a complete 'dockerization' is proof of concept for 'production' purpose.

### Running the application for development

via command line, in the root of the application folder enter the following commands:

`mvn clean install`

example: `C:\workspace\SpringBootCamelStreamsExample>mvn clean install`

create the docker container for the postgres database and launch it:

`docker-compose -f docker-compose.yml up`

example: `C:\workspace\SpringBootCamelStreamsExample>docker-compose -f docker-compose.yml up`

with the database container running the application can now be started with:

`java -jar target/SpringBootCamelStreamsExample-0.0.1-SNAPSHOT.jar`

example: `C:\workspace\SpringBootCamelStreamsExample>java -jar target/SpringBootCamelStreamsExample-0.0.1-SNAPSHOT.jar`

### Running the application and the database with docker

The following will create the docker container for the webapp and the database.

modify the content of:
`\src\main\resources\application.yml` 
with the one present in:
`\src\main\resources\application-configuration-for-complete-docker.sample` 

This change is needed due to different database connection configuration.

via command line, in the root of the application folder enter the following commands:

`mvn clean install`

example: `C:\workspace\SpringBootCamelStreamsExample>mvn clean install`

create the docker container and launch it:

`docker-compose -f docker-compose-complete.yml up`

example: `C:\workspace\SpringBootCamelStreamsExample>docker-compose -f docker-compose-complete.yml up`


## How to use the application

* To import data via **CSV** file use the link [http://localhost:8080/uploadCSV](http://localhost:8080/uploadCSV)
* To import data via **PRN/Fixed length** file use the link [http://localhost:8080/uploadPRN](http://localhost:8080/uploadPRN)

The html table with the persons balance data can be found at [http://localhost:8080](http://localhost:8080),
the page support pagination and is possible to customize the page size by setting the value in the top left corner;
creation and deletion of a person can be done in this page as well.

The application support also scanning of directory to search and import **CSV** and **PRN** files;
in order to enable it and configure the directories please modify the *application.yml* file accordingly (see snippet below)
for any changes to be applied redo the steps used to build and run the application in the sections above.

      file:
        csv:
          enable: true
          dir: C:/Users/Paizo/Desktop
          noop: true
          recursive: false
          type: .*.csv
          delay: 10000
        prn:
          enable: true
          dir: C:/Users/Paizo/Desktop
          noop: true
          recursive: false
          type: .*.prn
          delay: 10000 

Above: sample configuration to import csv and prn file from the directory `C:/Users/Paizo/Desktop` with a delay of 10 seconds

## Distributed Tracing

Considering this as an example of a *microservice* then it would be hard to troubleshooting problems across multiple microservices.
The application has been made ready for distributed tracing among different services by adding Spring Sleuth.

## Monitoring

Monitoring has been enabled and the health check endpoint is reachable at [http://localhost:9001/health](http://localhost:9001/health),
other monitoring endpoints are not reachable due to the lack of an authentication mechanism.

Copyright (C) 2018 by Francesco Pizzo.
