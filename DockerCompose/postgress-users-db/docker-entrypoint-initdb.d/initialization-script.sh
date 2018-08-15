# Starting the DB server to perform tables initialization
su - postgres -c "${POSTGRES} -D ${DATADIR} -c config_file=${CONF} ${LOCALONLY} &";

# Waiting for server start-up
until su - postgres -c "psql -l"; do
    echo "Starting up server to initialize tables..."
	sleep 1
done

echo "Connecting to db and submitting initialization sql statements...";

# Setting up environment variable for psql authentication
export PGPASSWORD=group2;

# Connecting to gis DB as user group2 and providing the initialization SQL (to create a second DB instance)
psql -h localhost -p 5432 -U group2 -d usersdb -f /docker-entrypoint-initdb.d/init.sql;
psql -h localhost -p 5432 -U group2 -d clientsdb -f /docker-entrypoint-initdb.d/init_client.sql

# Shutting down DB server
PID=`cat $PG_PID`;
while kill -TERM ${PID}; do
    echo "Waiting for the server to shut down..."
    sleep 1
done
