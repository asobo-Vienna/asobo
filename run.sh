#!/bin/bash
docker-compose up -d
java -jar ./asobo-0.0.1-SNAPSHOT.jar --spring.profiles.active=populate-database