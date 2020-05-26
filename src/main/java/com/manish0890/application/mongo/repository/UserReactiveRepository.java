package com.manish0890.application.mongo.repository;

import com.manish0890.application.mongo.document.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserReactiveRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByPhone(String phone);

    Flux<User> findByZipOrderByCreatedDateDesc(String zip);
}