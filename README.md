README

Clone the repository in your local environment and checkout to branch "master"

Using Docker to start the project - the easy and most convenient way

The project is fully dockerized, so if you have Docker installed and want to use Docker, you can spin up the client-side, server-side and both CDM and Collection-Manager databases and connect to server to them by using the command “docker-compose up” in the /docker folder of the repository. 

1. Thus, from the root directory you need to run:
- cd docker
- docker-compose up

2. After that wait a bit for all containers to be ready and when ready, you can navigate to “http://localhost:8080/ “ in your favourite browser.

3. To add some test data to the database, click on the button "Simulate biobanks sending data".

4. As an example for testing, you can search by the following concept IDs:
* 4265453 (age), with values: 60-80
* 3016958 (Epidermal growth factor receptor Ag [Presence] in Tissue), with values: 15-20

5. In the aforementioned example for searching, I used range values, so you need to switch on the “Range Query”in the UI.

6. You are ready to go, press the search button and analyse the results. You just got a ranked list of collections and biobanks with a hit ratio and number of hits matched for each of them. Contact the biobank whose collection fits your research purposes best and keep on innovating the medical field!  

If you are not using Docker and want to manually start everything - the hard and inconvenient way

If you do not wish to use Docker for any reason, here are the steps to replicate the environment and start the project:

1. To start the client:
	In a new terminal, navigate to DATABANKS-DEMO-PROJECT/fronted and run the command:
    - Npm run serve

2. To start the server:
	In a new terminal, navigate to DATABANKS-DEMO-PROJECT/backend and run the command:
    - Npm run dev

3. In your browser, go to http://localhost:8080/ to use the client.

4. If you want to test the system by querying the database, you will need to import the databases provided in the zip file (“cdm_dump.sql” and “collection_manager_db.sql”) in your PgAdmin (localhost). 

5. The CDM db needs to be setup with the following config:
- host: "localhost",
- port: 5432,
- user: "postgres",
- database: "cdm"

6. The collection manager db needs to be setup with the following config:
- host: "localhost",
- port: 5432,
- user: "postgres",
- database: "collection-manager”

7. If you want to import the database files in a different location or with different names, please adjust the config of the communication with the corresponding databases in the files: “backend/server/dao/cdm.js” and “backend/server/dao/collectionsDatabase.js”.

8. If you are not sure how to restore a Postgres database from a backup sql file, watch this video (from minute: 04:14): https://www.youtube.com/watch?v=S108Rh6XxPs&ab_channel=Learning

9. The client runs on port 8080 and the api runs in port 5001. Make port 5001 is free, or change it to your desired port in file: backend/.env

10. Then, proceed with step 3 from above.