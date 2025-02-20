curl -sSL --get --cookie /home/nicolas/cookie.jar --cookie-jar /home/nicolas/cookie.jar \
  --data-urlencode "client_id=myclient" \
  --data-urlencode "redirect_uri=http://localhost:8080" \
  --data-urlencode "scope=openid" \
  --data-urlencode "response_type=code" \
  http://localhost:8080/realms/myrealm/protocol/openid-connect/auth