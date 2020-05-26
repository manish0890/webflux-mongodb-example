# webflux-mongodb-example

User Management System Backend (RESTful service)    
Powered by Spring Webflux framework

https://manish0890.github.io/webflux-mongodb-example/   
More goodies coming up.....   

### System Requirements
- Unix based system
- Java 13
- Docker

### Scripts/Commands to run application

Start Local MongoDB Repository
```bash
./bin/docker-run-mongodb.sh
```

Pull project dependencies 
```bash
./mvnw clean install -DskipTests=true
```

Run Unit Tests
```bash
./mvnw clean test
```

Run Integration Tests
```bash
./mvnw clean -P integration-test test
```

Start Application
```bash
SPRING_PROFILE=dev ./mvnw clean spring-boot:run
```

Once the application is running the REST documentation and tester is available via Swagger at 
[http://localhost:8090/api/v1/service/swagger-ui.html](http://localhost:8090/api/v1/service/swagger-ui.html)
