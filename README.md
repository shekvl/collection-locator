# Searching in k-anoymized data
This project is part of a system which allows users to anonymize data to later search in them with a query interface.

his project represents the first out of four modules which is called 'Module 1: Index Definition ' 

This project is part of my proof of concept of the master-thesis with the topic 'Searching in k-anonymized data'.

This project consists of a spring boot application with a thymelaef frontend, as well as using docker for easier deployment.

## tl;dr
```
docker network create ano-system
docker-compose up --build
```

## Prerequisite 
- Docker
- docker compose

## Deployment
To start the system, only two commands are necessary , since the project comes with a pre-packaged .jar file, no compilation needs to be done.

The two commands create a new docker network, as well as start the application. Both commands need to be executed in the project folder.

```
docker network create ano-system
docker-compose up --build
```

To shut down the application the following command can be used
```
docker-compose down
```

### URLs and Ports
The application exposes two different url and port combination for users and developers.

#### Application
The first one is the application itself, which runs on `localhost:8080/index`.

#### Database
The second one is the database administration tool, which runs on `localhost:8081`.
- Database system -> MySQL
- Server -> indexgendb
- User -> root
- Password -> admin
- Database -> indexgen

## Configuration
To change the configuration of the application the following files might be interesting.

In these files ports, urls and usernames can be changed. 
The values in these files need to correspond to each other in order for the application to work.

- Spring Boot Application Properties -> `app/src/main/resources/application.properties` 
- Spring Boot Docker File -> `app/Dockerfile` 
- Application Docker Compose File -> `docker-compose.yml` 

## Recompile
Should any changes be applied to the application files, the spring boot application needs to be recompiled. 

This can be achieved by the following command, which needs to be executed inside the app folder.
```
mvnw clean package -DskipTests
```

After that, the docker container needs to be restarted with the commands, described above. 

### License
MIT

Copyright (c) 2022 Patrick Eisenkeil

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
OR OTHER DEALINGS IN THE SOFTWARE.