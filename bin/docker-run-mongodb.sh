#!/bin/bash

# Define mongoDB docker image version and port number
mongoTag=4.0;
mongoPort=27017;

# Remove existing container if it exists
docker rm -f local-mongodb;

# Start a new Mongo DB
docker run -d --name local-mongodb -p ${mongoPort}:${mongoPort} \
-e MONGO_INITDB_ROOT_USERNAME=root \
-e MONGO_INITDB_ROOT_PASSWORD=example \
 mongo:${mongoTag};
