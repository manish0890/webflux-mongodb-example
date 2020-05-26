# webflux-mongodb-example

User Management System Backend (RESTful service)    
Powered by Spring Webflux framework

https://manish0890.github.io/webflux-mongodb-example/   
More goodies coming up.....   

### System Requirements
- Unix based system
- Java 13
- Docker
- Docker-compose

### Scripts/Commands to run application

Start Local MongoDB Repository
```shell script
./bin/docker-run-mongodb.sh
```

Pull project dependencies 
```shell script
./mvnw clean install -DskipTests=true
```

Run Unit Tests
```shell script
./mvnw clean test
```

Run Integration Tests
```shell script
./mvnw clean -P integration-test test
```

Start Application using maven wrapper
```shell script
SPRING_PROFILE=dev ./mvnw clean spring-boot:run
```

Start Application usng docker-compose    
(make sure nothing is running on port 27017)
```shell script
./bin/start-service-with-db.sh
```
Stop All service and db (This will not delete data)
```shell script
docker-compose down
```
top All services and db and remove data
```shell script
docker-compose down -v
```

Once the application is running the REST documentation and tester is available via Swagger at 
[http://localhost:8090/api/v1/service/swagger-ui.html](http://localhost:8090/api/v1/service/swagger-ui.html)
