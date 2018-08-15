#!/usr/bin/env bash
if [ "$1" = "windows" ]
then
    # Killing running containers and performing clean-up
    docker stop usersdb_instance positionsdb_instance
    docker rm -v -f usersdb_instance positionsdb_instance

    # Building and running dbs as maven package executes tests before building the jar
    cd ./DockerCompose/postgress-users-db/
    docker build -t postgress_db -f Dockerfile.usersdb .
    cd ../mongo-positions-db/
    docker build -t mongo_db -f Dockerfile.positionsdb .
    cd ../..

    docker run -d -p 5432:5432 --name usersdb_instance -v users_db:/var/lib/postgresql postgress_db
    docker run -d -p 27017:27017 --name positionsdb_instance -v positions_db:/data/db mongo_db

    # Making sure that both containers are running before launching maven
    sleep 10s

    # Running tests and packages everything into a jar
    mvn clean install
    cd backend
    mvn spring-boot:run

    # Finally closing all containers
    docker stop usersdb_instance positionsdb_instance

else
    set -x
    # Killing running containers and performing clean-up
    sudo docker stop usersdb_instance positionsdb_instance
    sudo docker rm -v -f usersdb_instance positionsdb_instance

    # Building and running dbs as maven package executes tests before building the jar
    cd ./DockerCompose/postgress-users-db/
    sudo docker build -t postgress_db -f Dockerfile.usersdb .
    cd ../mongo-positions-db/
    sudo docker build -t mongo_db -f Dockerfile.positionsdb .
    cd ../..

    sudo docker run -d -p 5432:5432 --name usersdb_instance -v users_db:/var/lib/postgresql postgress_db
    sudo docker run -d -p 27017:27017 --name positionsdb_instance -v positions_db:/data/db mongo_db

    # Making sure that both containers are running before launching maven
    sleep 10s

    # Running tests and packages everything into a jar
    mvn clean install

    cd backend

    mvn spring-boot:run

    # Finally closing all containers
    docker stop usersdb_instance positionsdb_instance
fi