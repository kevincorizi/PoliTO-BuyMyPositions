## HOW IT WORKS

We exploit docker and an alpine image to create a "link" between the named volumes of the DBs
and the db_archive directory. In docker backup.sh through the alpine instance we create archives 
of the volumes data and store them on in the volume associated with the db_archive directory. 

Credits to [this guy](https://stackoverflow.com/questions/38298645/how-should-i-backup-restore-docker-named-volumes) for the great explanation.


## Create a clean dump
1.  Clean all volumes and containers 
    - `docker container ls -a`
    - `docker container rm <names of our containers>`
    - `docker volume prune`

2. Build containers and run application
3. Perform all needed ops 
4. `IMPORTANT:` take care of logging out or you will get a dump in a non stable state
5. Run `./docker_backup.sh`
6.  - Perform again step 1
    - Uncomment line 18 (docker_restore.sh) to enable automatic restoring from
    backup (whic is performed only when no volumes are present). 
    - You can also comment last line of
    Dockerfile.usersdb and rebuild the image as initialization scripts are no longer needed.
