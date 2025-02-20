#!/bin/bash
echo "*** build.sh: Stoping and removing the running containers, if any"
mvn -pl infra clean

echo "*** build.sh: Starting the Keycloak server"
mvn -pl infra docker-compose up -d keycloak

until curl --head http://localhost:8080/health/ready -sf -o /dev/null;
do
  sleep 5
done
sleep 5
docker exec -it keycloak /opt/keycloak/customization/customize.sh $KEYCLOAK_PORT
mvn -pl -infra clean install
