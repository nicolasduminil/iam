curl --location --request POST "http://localhost:8080/auth/realms/myrealm/protocol/openid-connect/token" \
--header "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "password=California1" \
--data-urlencode "username=nicolas" \
--data-urlencode "client_id=myclient" \
--data-urlencode "grant_type=password"