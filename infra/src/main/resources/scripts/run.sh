#!/bin/bash
MAX_RETRIES=30
COUNTER=0
KEYCLOAK_HOST=$(./src/main/resources/scripts/get-keycloak-ip-address.sh)
KEYCLOAK_PORT=$(./src/main/resources/scripts/get-keycloak-http-port.sh)
until curl -s http://localhost:8080/auth > /dev/null
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
echo ">>> Keycloak is up and running on $KEYCLOAK_HOST:$KEYCLOAK_PORT !"
docker exec keycloak /opt/keycloak/customization/customize.sh $KEYCLOAK_HOST:$KEYCLOAK_PORT
echo ">>> Keycloak customization completed."