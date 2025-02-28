#!/bin/bash
KCADM=/opt/keycloak/bin/kcadm.sh
$KCADM config credentials --server http://$1 --realm master --user admin --password admin
$KCADM create realms -s realm=myrealm -s enabled=true
$KCADM create users -r myrealm -s username=john -s enabled=true -s "emailVerified=true" \
  -s "email=john.doe@emailcom" -s "firstName=John" -s "lastName=Doe"
$KCADM set-password -r myrealm --username john --new-password password1
$KCADM create users -r myrealm -s username=jane -s enabled=true -s "emailVerified=true" \
  -s "email=jane.doe@emailcom" -s "firstName=Jane" -s "lastName=Doe"
$KCADM set-password -r myrealm --username jane --new-password password1
#$KCADM create clients -r myrealm -f /opt/keycloak/customization/spa-client.json
$KCADM create clients -r myrealm -f /opt/keycloak/customization/fe-client.json
#$KCADM create clients -r myrealm -f /opt/keycloak/customization/be-client.json
$KCADM create roles -r myrealm -s name=manager
$KCADM add-roles --uusername john --rolename manager -r myrealm
$KCADM create roles -r myrealm -s name=employee
$KCADM add-roles -r myrealm --uusername jane --rolename employee