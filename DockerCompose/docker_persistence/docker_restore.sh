#!/usr/bin/env bash

sudo docker stop usersdb_instance positionsdb_instance
sudo docker rm -v -f usersdb_instance positionsdb_instance

echo ++ Restore - Restoring users_db volume...
docker run --rm \
  --volume users_db:/tmp/volume \
  --volume $(pwd)/db_archive:/tmp/backup \
  alpine \
  tar xf /tmp/backup/usersdb_dump.tar -C /tmp/volume --strip 1

echo ++ Restore - Restoring positions_db volume...
docker run --rm \
  --volume positions_db:/tmp/volume \
  --volume $(pwd)/db_archive:/tmp/backup \
  alpine \
  tar xf /tmp/backup/positionsdb_dump.tar -C /tmp/volume --strip 1

echo ++ Restore - Completed!