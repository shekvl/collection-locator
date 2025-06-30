# Colection Locator README
## Overview

The `Collection Locator` offers a way to search for collections in a distributed database system which includes a wide variety of different collection schemas and attribute ontologies, by providing a repository for semantically annotated collection schemas together with additional quality metadata.

This project was developed as part of the WP2 of Austrian BBMRI node. It consists of:
- a `client` providing a user interface to upload and browse the Collection Locator `repository`
- a `database` that utilizes the `OMOP Common Data Model` which is used for annotation and holds the repository
- a `server` to host the project

### Readme:
- [Client](./client/README.md)

- [Database](./db/README.md)

- [Server](./server/README.md)


## Project Setup

Install all `node dependencies` of the project
```
npm run init
```

## Run

Serve `client` and `server` separately
```
npm run client
```
```
npm run server
```

Serve both `client` and `server` concurrently in the same terminal
```
npm run serve
```

## Production

`Compile` client and store the static files in the configured output directory.
```
npm run build
```

Runs server in `production` mode with static client files.
```
npm run deploy
```

`Install` dependencies, `build` client and `run` server for production (after changes in source code e.g. pull from repo)
```
npm run redeploy
```