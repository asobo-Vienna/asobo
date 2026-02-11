@echo off

REM start Docker
docker-compose up -d

REM generate JWT_SECRET using PowerShell
for /f %%i in ('powershell -Command "[Convert]::ToBase64String((1..64 | %% {Get-Random -Max 256}))"') do set JWT_SECRET=%%i

REM start the Spring Boot app
java -jar asobo-0.0.1-SNAPSHOT.jar --spring.profiles.active=populate-database

pause