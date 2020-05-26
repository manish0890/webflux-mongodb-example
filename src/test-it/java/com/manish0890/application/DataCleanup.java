package com.manish0890.application;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.util.List;

// This class cleans the test data after each test
public abstract class DataCleanup {
    @Autowired
    List<ReactiveMongoRepository> repositories;

    @BeforeEach
    public void cleanUpRepositories() {
        repositories.forEach(repository -> repository.deleteAll().block());
    }
}