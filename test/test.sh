ACCESS_TOKEN=$(curl -k http://client:secret@localhost:9000/oauth2/token -d grant_type=client_credentials -d scope="read write" -s | jq .access_token -r)

echo $ACCESS_TOKEN

curl -H "accept:application/json" -k http://localhost:9000/eureka/apps -s

echo -e ""

curl -H "Authorization: Bearer $ACCESS_TOKEN" -k http://localhost:9000/api/product