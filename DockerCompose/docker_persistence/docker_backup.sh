#!/usr/bin/env bash

## Removes previous archives and creates tar backup of both dbs
echo ++ Backup - Deleting old archives...
sudo rm db_archive/usersdb_dump.tar
sudo rm db_archive/positionsdb_dump.tar

echo ++ Backup - Creating backup of users_db...
sudo docker run --rm \
  --volume users_db:/tmp/volume \
  --volume $(pwd)/db_archive:/tmp/backup \
  alpine \
  tar cf /tmp/backup/usersdb_dump.tar /tmp/volume

echo ++ Backup - Creating backup of positions_db...
sudo docker run --rm \
  --volume positions_db:/tmp/volume \
  --volume $(pwd)/db_archive:/tmp/backup \
  alpine \
  tar cf /tmp/backup/positionsdb_dump.tar /tmp/volume

echo ++ Backup - Completed!