# TEST SUITE README
## WARNING 

The test suites perform integration tests of all major functionalities of the REST API.
This means that even though they exploit a mocked web environment and MockMvc to simulate web interactions
all the underlying layers are not mocked and exploit the actual components of the application.

Tests are defined in a way to leave real DBs in a clean sate as much as possible.
Anyway tests on transactions leave some data on the DB and modify the balance of Test users.

In order to restore the initial test environment it is possible to exploit the docker_restore.sh
to revert DBs to the state of last backups.


