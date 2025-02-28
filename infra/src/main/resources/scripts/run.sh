#!/bin/bash
docker-compose --file ./src/main/resources/docker-compose.yaml down
docker-compose --file ./src/main/resources/docker-compose.yaml up -d keycloak
MAX_RETRIES=30
COUNTER=0
until curl localhost:8080 -sf -o /dev/null;
do
  sleep 5
  COUNTER=$((COUNTER + 1))
  if [ $COUNTER -eq $MAX_RETRIES ]
  then
    echo ">>> Failed to connect to Keycloak. We cant't continue."
    exit 1
  else
    echo ">>> Waiting for Keycloak to start... ($COUNTER/$MAX_RETRIES)"
  fi
done
sleep 6
echo ">>> Keycloak is up and running !"
docker exec keycloak /opt/keycloak/customization/customize.sh localhost:8080
echo ">>> Keycloak customization completed."
docker-compose --file ./src/main/resources/docker-compose.yaml up -d iam-backend iam-frontend