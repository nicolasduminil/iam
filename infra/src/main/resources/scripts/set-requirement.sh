# Create a temporary script in the container
docker exec keycloak bash -c 'cat > /tmp/update_flow.sh << "EOF"
#!/bin/bash
COOKIE_ID=$1
TOKEN=$(curl -s -X POST http://localhost:8080/realms/master/protocol/openid-connect/token \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" | grep -o "\"access_token\":\"[^\"]*\"" | sed "s/\"access_token\":\"//g" | sed "s/\"//g")

curl -X PUT http://localhost:8080/admin/realms/myrealm/authentication/flows/browser-passwordless-webauthn/executions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"$COOKIE_ID\",\"requirement\":\"ALTERNATIVE\"}"
EOF'

# Make it executable
docker exec keycloak chmod +x /tmp/update_flow.sh

# Run it with the Cookie ID
docker exec keycloak /tmp/update_flow.sh $COOKIE_EXECUTION_ID
