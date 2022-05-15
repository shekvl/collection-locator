# Searching in k-anoymized data
This project is part of a system which allows users to anonymize data to later search in them with a query interface.

his project represents the first out of four modules which is called 'Module 2: Anonymization' 

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
The first one is the application itself, which runs on `localhost:8082/index`.

- Collections
    - Add new Collection (Lets the user add a new collection)
    - For each collection
        - Open Collection (Lets the user see more data about the collection)
            - Edit Collection (Lets the user see edit the collection)
        - Delete Collection (Lets the user delete a collection)
- Definitions
    - Add Definition (Lets the user add a new definition in JSON format)
    - For each definition
        - Open Definition (Lets the user see more data of a definition)
        - Delete Definition (Lets the user delete a definition)
- Anonymized Datasets
    - New Anonymized Dataset (Lets the user create a new anonymized dataset based on a collection)
    - New Combination (Lets the user create a new anonymized dataset based on a collection and a definition)
    - For each Anonymized Dataset
        - Start anonymization (Lets the user start the anonymization process for one anonymized dataset)
        - Download anonymized files (Lets the user download the anonymized data)
        - Open anonymized dataset (Lets the user see more data of an anonymized dataset)
            - Edit anonymized dataset (Lets the user edit properties an anonymized dataset)
            - Edit anonymized dataset columns (Lets the user edit the columns an anonymized dataset)
        - Delete anonymized dataset (Lets the user delete an anonymized dataset)
- Actions
    - Import Definitions (Pulls all definitions from module 1)
    - Create Datasets (Creates all datasets based on collections and definitions (Clears all present datasets !!!!))
    - Anonymize All (Anonymizes all datasets)
    - Send anonymized Datasets XML (Pushes all anonymized datasets to module 3 in XML format)
    - Send anonymized Datasets JSON (Pushes all anonymized datasets to module 3 in JSON format)
    - Send all data XML (Executes all steps described above and sends the data in XML format to module 3)
    - Send all data JSON (Executes all steps described above and sends the data in JSON format to module 3)
    - Get XML schema (Gets the XML Schema from the anonymized datasets that get sent to module 3)
- Options
    - Biobank name (Unique name to connect anonymized datasets to biobanks)
    - URL of index generation (Module 1) (URL of the API Endpoint that provides the definition in XML Format of module 1)
    - URL of index collection (Module 3) (URL of the API Endpoint that collects the anonymized datasets)

Example Files can be found at the project root in the folder `ExampleFiles`
#### Database
The second one is the database administration tool, which runs on `localhost:8083`.
- Database system -> MySQL
- Server -> anodb
- User -> root
- Password -> admin
- Database -> ano

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