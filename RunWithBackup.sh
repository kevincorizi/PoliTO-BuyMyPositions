#!/usr/bin/env bash
set -x

# This script rebuild all containers, eventually restores DBs in case the corresponding volumes are not present
# Finally starts containers, performs a clean install and runs the application.

# Killing running containers
sudo docker stop usersdb_instance positionsdb_instance


# Building and running dbs as maven package executes tests before building the jar
cd ./DockerCompose/postgress-users-db/
sudo docker build -t postgress_db -f Dockerfile.usersdb .
cd ../mongo-positions-db/
sudo docker build -t mongo_db -f Dockerfile.positionsdb .
cd ../..

# Restore/Create db volumes from backups if they do not exist
if [[ ! $(docker volume ls | grep users_db) && ! $(docker volume ls | grep positions_db) ]]; then
    cd ./DockerCompose/docker_persistence
       docker_restore.sh
    cd ../..
fi

sudo docker run -d -p 5432:5432 --name usersdb_instance -v users_db:/var/lib/postgresql postgress_db
sudo docker run -d -p 27017:27017 --name positiondb_instance -v positions_db:/data/db mongo_db

# Making sure that both containers are running before launching maven
sleep 15s

# Running tests and packages everything into a jar
mvn clean install

cd backend

mvn spring-boot:run

# Finally closing all containers
sudo docker stop usersdb_instance positionsdb_instance