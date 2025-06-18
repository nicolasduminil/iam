#!/bin/bash
create_keycloak_client()
{
  local client_config_file="$1"
  local secret_output_file="$2"
  local client_id
  local client_secret
  local realm_name="${3:-myrealm}"

  if [ ! -f "$client_config_file" ]
  then
    echo "### Error: Client configuration file not found: $client_config_file"
    return 1
  fi
  client_id=$($KCADM create clients -r "$realm_name" -f "$client_config_file" 2>&1 \
    | grep -o "id '[^']*'" | cut -d"'" -f2)
  if [ -z "$client_id" ]
  then
    echo "### Error: Failed to create client or extract client ID"
    return 1
  fi
  client_secret=$($KCADM get clients/"$client_id"/client-secret -r "$realm_name" \
    | grep value | cut -d'"' -f4)
  if [ -z "$client_secret" ]
  then
    echo "### Error: Failed to retrieve client secret"
    return 1
  fi
  echo "$client_secret" > "$secret_output_file"
  return 0
}

export KCADM=/opt/keycloak/bin/kcadm.sh
$KCADM config credentials --server http://$1 --realm master --user admin --password admin
$KCADM create realms -s realm=myrealm -s enabled=true
$KCADM create users -r myrealm -s username=john -s enabled=true -s "emailVerified=true" \
  -s "email=john.doe@emailcom" -s "firstName=John" -s "lastName=Doe"
$KCADM set-password -r myrealm --username john --new-password password1
$KCADM create users -r myrealm -s username=jane -s enabled=true -s "emailVerified=true" \
    -s "email=jane.doe@emailcom" -s "firstName=Jane" -s "lastName=Doe"
$KCADM set-password -r myrealm --username jane --new-password password1
create_keycloak_client \
  "/opt/keycloak/customization/fe-acc.json" \
  "/opt/keycloak/.fe-acc-secret"
create_keycloak_client \
  "/opt/keycloak/customization/fe-ropc.json" \
  "/opt/keycloak/.fe-ropc-secret"
create_keycloak_client \
  "/opt/keycloak/customization/fe-sac.json" \
  "/opt/keycloak/.fe-sac-secret"
$KCADM create roles -r myrealm -s name=manager
$KCADM create roles -r myrealm -s name=webauthn-users
$KCADM add-roles --uusername john --rolename manager -r myrealm
$KCADM add-roles --uusername jane --rolename manager -r myrealm
$KCADM add-roles -r myrealm --uusername jane --rolename webauthn-users
$KCADM add-roles --uusername service-account-fe-sac -r myrealm --rolename manager
/opt/keycloak/customization/ex.sh