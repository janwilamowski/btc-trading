## Simple Bitcoin Trading API

### How to run

* requires JDK 21
* build with maven: `./mvnw package`
* start local server: `./mvnw spring-boot:run`
* access http://localhost:8080/users or (better) through a tool like Postman

### How to build and run a Docker image

* build locally: `./mvnw package`
* build and start containers: `docker-compose up --build`
* access http://localhost:8080/users

### Features

* constantly changing BTC price
* automatically generated API documentation, see docs.html

### Missing features

* proper database (at the moment, it uses in-memory H2)
* authentication
