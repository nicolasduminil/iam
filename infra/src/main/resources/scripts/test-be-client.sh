curl -L -X POST "http://localhost:8080/realms/myrealm/protocol/openid-connect/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "client_id=be" \
--data-urlencode "grant_type=password" \
--data-urlencode "client_secret=2TEduNWKaQBbhI97AHJuKTNctODkfH0o" \
--data-urlencode "scope=openid" \
--data-urlencode "username=john" \
--data-urlencode "password=password1"