# start Docker
docker-compose up -d

# generate JWT_SECRET (64 bytes, Base64)
$env:JWT_SECRET = [Convert]::ToBase64String((1..64 | ForEach-Object {Get-Random -Maximum 256}))

# start the Spring Boot app with populate-database profile
java -jar .\asobo-0.0.1-SNAPSHOT.jar --spring.profiles.active=populate-database

pause