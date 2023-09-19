mvn clean install
until curl http://localhost:8080/health/ready -sf -o /dev/null;
do
  sleep 5
done
sleep 5
docker exec -it keycloak /opt/keycloak/customization/customize.sh $KEYCLOAK_PORT
