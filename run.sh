#!/bin/bash
# start Docker
docker-compose up -d

# generate JWT_SECRET (64 bytes, Base64)
export JWT_SECRET=$(openssl rand -base64 64)

# start the Spring Boot app with populate-database profile
java -jar ./asobo-0.0.1-SNAPSHOT.jar --spring.profiles.active=populate-database
