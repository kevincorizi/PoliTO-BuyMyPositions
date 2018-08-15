#!/usr/bin/env bash

#Start all the containers
sudo docker run -d -p 5432:5432 --name usersdb_instance -v users_db:/var/lib/postgresql postgress_db
sudo docker run -d -p 27017:27017 --name positionsdb_instance -v positions_db:/data/db mongo_db
