curl --location --request POST "http://localhost:8080/realms/myrealm/protocol/openid-connect/token" \
--header "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "password=password1" \
--data-urlencode "username=john" \
--data-urlencode "client_id=oauth-playground" \
--data-urlencode "grant_type=password"