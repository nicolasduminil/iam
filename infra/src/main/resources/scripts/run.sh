#!/bin/bash
docker run --name keycloak --rm -d \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  -v $(pwd)/src/main/resources/fe-acc.json:/opt/keycloak/customization/fe-acc.json \
  -v $(pwd)/src/main/resources/fe-ropc.json:/opt/keycloak/customization/fe-ropc.json \
  -v $(pwd)/src/main/resources/fe-sac.json:/opt/keycloak/customization/fe-sac.json \
  -v $(pwd)/src/main/resources/scripts/customize.sh:/opt/keycloak/customization/customize.sh \
  --network host \
  quay.io/keycloak/keycloak:latest start-dev > /dev/null 2>&1
MAX_RETRIES=10
COUNTER=0
until curl localhost:8080 -sf -o /dev/null;
do
  sleep 5
  COUNTER=$((COUNTER + 1))
  if [ $COUNTER -eq $MAX_RETRIES ]
  then
    echo ">>> Failed to connect to Keycloak. We can't continue."
    exit 1
  else
    echo ">>> Waiting for Keycloak to start... ($COUNTER/$MAX_RETRIES)"
  fi
done
sleep 6
echo ">>> Keycloak is up and running !"
docker exec keycloak /opt/keycloak/customization/customize.sh localhost:8080
echo ">>> Keycloak customization completed."
export FE_ACC_SECRET=$(docker exec keycloak cat /opt/keycloak/.fe-acc-secret)
export FE_ROPC_SECRET=$(docker exec keycloak cat /opt/keycloak/.fe-ropc-secret)
export FE_SAC_SECRET=$(docker exec keycloak cat /opt/keycloak/.fe-sac-secret)
docker run --name iam-frontend --rm -d --network host \
  -e FE_ACC_SECRET=$FE_ACC_SECRET -e FE_ROPC_SECRET=$FE_ROPC_SECRET \
  -e FE_SAC_SECRET=$FE_SAC_SECRET \
  nicolasduminil/iam-front-end:1.0-SNAPSHOT > /dev/null 2>&1
docker run --name iam-backend --rm -d --network host nicolasduminil/iam-back-end:1.0-SNAPSHOT > /dev/null 2>&1
