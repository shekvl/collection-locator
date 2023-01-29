## Features
The server has access to the collection locator database which includes the omop cdm.

It is possible to upload new collections and their attributes to the locator and to download randomly generated collections as spreadsheets.

Currently, it is possible to query collections that contain one or all concepts of a list of concept ids.

## Table Functions
To keep the codebase simple and to separate database from server, several sql table functions have been implemented that encapsulate complex queries which can be executed by calling the respective table function signature.

JS functions within tableFunctions.ts wrap these table function calls and make them accessible inside the code base.

## Create global .env file
`Server port` and `db connection details` have to be specified as environment variables.

Example:
```
SERVER_PORT=3000

PG_HOST="localhost"
PG_USER="postgres"
PG_DATABASE="collection_locator"
PG_PORT="5432"
PG_PASSWORD="password"
```

## Dependencies
`cors` provides a express middleware that enables CORS

`dotnet` manages environment variables

`multer` is a middleware that handles file upload of multipart/form-data.

`fast-csv` provides a library to parse and create csv files.

`helmet` sets HTTP headers that help protect from well-known web vulnerabilities.

`compression` compresses the HTTP response sent back to a client to save bandwidth and making loading the page faster.