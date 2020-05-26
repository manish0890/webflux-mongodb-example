package com.manish0890.application.mongo.repository;

import com.manish0890.application.DataCleanup;
import com.manish0890.application.TestConfig;
import com.manish0890.application.mongo.document.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static com.manish0890.application.util.TestUtility.userWithTestValues;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class UserReactiveRepositoryIntegrationTest extends DataCleanup {

    @Autowired
    private UserReactiveRepository repository;

    @Test
    void testFindByPhone() {
        User testUser = userWithTestValues();

        // Create and Verify created user
        StepVerifier.create(repository.save(testUser))
                .expectSubscription()
                .consumeNextWith(user -> {
                    assertNotNull(user.getId());
                    assertEquals(user, user);
                })
                .verifyComplete();

        // Verify User fetched using phone number
        StepVerifier.create(repository.findByPhone(testUser.getPhone()))
                .expectSubscription()
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void testFindByZip() {

        // Create two test user objects with same zip code
        User testUser1 = userWithTestValues();
        testUser1.setCreatedDate(123L);
        User testUser2 = userWithTestValues();
        testUser2.setCreatedDate(124L);
        testUser2.setZip(testUser1.getZip());

        // Create one test user with different zip code
        User testUser3 = userWithTestValues();
        testUser3.setCreatedDate(125L);

        // Create and Verify created user
        testUser1 = repository.save(testUser1).block();
        assertNotNull(testUser1.getId());
        testUser2 = repository.save(testUser2).block();
        assertNotNull(testUser2.getId());
        testUser3 = repository.save(testUser3).block();
        assertNotNull(testUser3.getId());

        // Verify User fetched using phone number
        StepVerifier.create(repository.findByZipOrderByCreatedDateDesc(testUser1.getZip()))
                .expectSubscription()
                // testUser2 should be fetched first since its created date is earlier
                .expectNext(testUser2)
                // testUser1 should be fetched next
                .expectNext(testUser1)
                // testUser3 should not be fetched at all
                .verifyComplete();
    }
}
