#!/bin/bash
KCADM=/opt/keycloak/bin/kcadm.sh
$KCADM config credentials --server http://localhost:8080 --realm master --user admin --password admin
$KCADM create realms -s realm=myrealm -s enabled=true
$KCADM create users -r myrealm -s username=john -s enabled=true
$KCADM set-password -r myrealm --username john --new-password password1
$KCADM create users -r myrealm -s username=jane -s enabled=true
$KCADM set-password -r myrealm --username jane --new-password password1
$KCADM create clients -r myrealm -s clientId=spa -s rootUrl="http://localhost:8080" -s "redirectUris=[\"/*\"]" -s "webOrigins=[\"+\"]" -s publicClient="true"
$KCADM create clients -r myrealm -s clientId=fe -s rootUrl="http://localhost:8080" -s "redirectUris=[\"/*\"]"
$KCADM create clients -r myrealm -s clientId=be -s directAccessGrantsEnabled="true" -s rootUrl="http://localhost:8080" -s "redirectUris=[\"/*\"]"
$KCADM create roles -r myrealm -s name=manager
$KCADM add-roles --uusername john --rolename manager -r myrealm
$KCADM create roles -r myrealm -s name=employee
$KCADM add-roles -r myrealm --uusername jane --rolename employee