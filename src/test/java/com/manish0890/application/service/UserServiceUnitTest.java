package com.manish0890.application.service;

import com.manish0890.application.exception.NotFoundException;
import com.manish0890.application.document.User;
import com.manish0890.application.repository.UserReactiveRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.manish0890.application.util.TestUtility.userWithAllValues;
import static com.manish0890.application.util.TestUtility.userWithTestValues;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserReactiveRepository repository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("User Create Tests")
    class CreateTests {

        @Test
        void testCreate() {
            User expectedUser = userWithTestValues();
            when(repository.findByPhone(eq(expectedUser.getPhone()))).thenReturn(Mono.empty());
            when(repository.save(eq(expectedUser))).thenReturn(Mono.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.create(expectedUser))
                    .expectSubscription()
                    .consumeNextWith(user -> {
                        assertNotNull(user);
                        assertNotNull(user.getCreatedDate());
                        assertNull(user.getUpdatedDate());
                        assertEquals(expectedUser, user);
                    })
                    .verifyComplete();
        }

        @Test
        void testCreateNullObject() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.create(null));
            assertEquals("User cannot be null", e.getMessage());
        }

        @Test
        void testCreateWithId() {
            User expectedUser = userWithTestValues();
            expectedUser.setId(randomAlphabetic(10));
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.create(expectedUser));
            assertEquals("Id must be empty", e.getMessage());
        }

        @Test
        void testCreateWithCreatedDate() {
            User expectedUser = userWithTestValues();
            expectedUser.setCreatedDate(123L);
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.create(expectedUser));
            assertEquals("Created Date must be null", e.getMessage());
        }

        @Test
        void testCreateWithUpdatedDate() {
            User expectedUser = userWithTestValues();
            expectedUser.setUpdatedDate(23432L);
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.create(expectedUser));
            assertEquals("Updated Date must be null", e.getMessage());
        }

        @Test
        void testCreateWithoutPhone() {
            User expectedUser = userWithTestValues();
            expectedUser.setPhone(null);
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.create(expectedUser));
            assertEquals("Phone number must be provided", e.getMessage());

            expectedUser.setPhone("");
            e = assertThrows(IllegalArgumentException.class, () ->
                    userService.create(expectedUser));
            assertEquals("Phone number must be provided", e.getMessage());
        }

        @Test
        void testCreateWithAlphabeticPhone() {
            User expectedUser = userWithTestValues();
            expectedUser.setPhone(randomAlphabetic(10));
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.create(expectedUser));
            assertEquals("Phone number has to be numeric value only", e.getMessage());
        }

        @Test
        void testCreateWithUsedPhoneNumber() {
            User expectedUser = userWithTestValues();
            when(repository.findByPhone(eq(expectedUser.getPhone()))).thenReturn(Mono.just(userWithAllValues()));
            when(repository.save(eq(expectedUser))).thenReturn(Mono.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.create(expectedUser))
                    .expectSubscription()
                    .consumeErrorWith(ex -> {
                        assertTrue(ex instanceof IllegalArgumentException);
                        assertEquals("Other User from our records is registered with the same phone number", ex.getMessage());
                    })
                    .verify();
        }
    }

    @Nested
    @DisplayName("User Read Tests")
    class ReadTests {

        @Test
        void testGetById() {
            User expectedUser = userWithAllValues();
            when(repository.findById(eq(expectedUser.getId()))).thenReturn(Mono.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.getById(expectedUser.getId()))
                    .expectSubscription()
                    .expectNext(expectedUser)
                    .verifyComplete();
        }

        @Test
        void testGetByIdNullString() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.getById(null));
            assertEquals("Id must not be blank or null", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () ->
                    userService.getById(""));
            assertEquals("Id must not be blank or null", e.getMessage());
        }

        @Test
        void testGetByNonExistentId() {
            when(repository.findById(anyString())).thenReturn(Mono.empty());

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.getById(randomAlphanumeric(20)))
                    .expectSubscription()
                    .consumeErrorWith(ex -> {
                        assertTrue(ex instanceof NotFoundException);
                        assertEquals("User does not exist in database", ex.getMessage());
                    })
                    .verify();
        }

        @Test
        void testGetByPhone() {
            User expectedUser = userWithAllValues();
            when(repository.findByPhone(eq(expectedUser.getPhone()))).thenReturn(Mono.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.getByPhoneNumber(expectedUser.getPhone()))
                    .expectSubscription()
                    .expectNext(expectedUser)
                    .verifyComplete();
        }

        @Test
        void testGetByPhoneNullString() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.getByPhoneNumber(null));
            assertEquals("Phone must not be blank or null", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () ->
                    userService.getByPhoneNumber(""));
            assertEquals("Phone must not be blank or null", e.getMessage());
        }

        @Test
        void testGetByNonExistentPhone() {
            when(repository.findByPhone(anyString())).thenReturn(Mono.empty());

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.getByPhoneNumber(randomAlphanumeric(20)))
                    .expectSubscription()
                    .consumeErrorWith(ex -> {
                        assertTrue(ex instanceof NotFoundException);
                        assertEquals("User does not exist in database", ex.getMessage());
                    })
                    .verify();
        }

        @Test
        void testGetByZip() {
            User expectedUser = userWithAllValues();
            when(repository.findByZipOrderByCreatedDateDesc(eq(expectedUser.getZip())))
                    .thenReturn(Flux.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.getByZip(expectedUser.getZip()))
                    .expectSubscription()
                    .expectNext(expectedUser)
                    .verifyComplete();
        }

        @Test
        void testGetByZipNullString() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.getByZip(null));
            assertEquals("Zip must not be blank or null", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () ->
                    userService.getByZip(""));
            assertEquals("Zip must not be blank or null", e.getMessage());
        }

        @Test
        void testGetByNonExistentZip() {
            when(repository.findByZipOrderByCreatedDateDesc(anyString())).thenReturn(Flux.empty());

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.getByZip(randomAlphanumeric(20)))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        void testGetAll() {
            User expectedUser = userWithAllValues();

            // Stub and also assert that sort is specified
            when(repository.findAll(eq(Sort.by("createdDate").descending())))
                    .thenReturn(Flux.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.getAll())
                    .expectSubscription()
                    .expectNext(expectedUser)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("User Update Tests")
    class UpdateTests {

        @Test
        void testUpdate() {
            User expectedUser = userWithTestValues();
            expectedUser.setId(randomAlphanumeric(10));

            when(repository.existsById(eq(expectedUser.getId()))).thenReturn(Mono.just(true));
            when(repository.findById(eq(expectedUser.getId()))).thenReturn(Mono.just(expectedUser));
            when(repository.findByPhone(eq(expectedUser.getPhone()))).thenReturn(Mono.empty());
            when(repository.save(eq(expectedUser))).thenReturn(Mono.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.update(expectedUser))
                    .expectSubscription()
                    .consumeNextWith(user -> {
                        assertNotNull(user);
                        assertNotNull(user.getUpdatedDate());
                        assertEquals(expectedUser, user);
                    })
                    .verifyComplete();
        }

        // Don't provide phone number in user object
        @Test
        void testUpdateWithoutPhoneNumber() {
            User expectedUser = userWithTestValues();
            expectedUser.setId(randomAlphanumeric(10));
            expectedUser.setPhone(null);

            when(repository.existsById(eq(expectedUser.getId()))).thenReturn(Mono.just(true));
            when(repository.findById(eq(expectedUser.getId()))).thenReturn(Mono.just(expectedUser));
            when(repository.save(eq(expectedUser))).thenReturn(Mono.just(expectedUser));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.update(expectedUser))
                    .expectSubscription()
                    .consumeNextWith(user -> {
                        assertNotNull(user);
                        assertNotNull(user.getUpdatedDate());
                        assertEquals(expectedUser, user);
                    })
                    .verifyComplete();
        }

        @Test
        void testUpdateNullObject() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.update(null));
            assertEquals("User cannot be null", e.getMessage());
        }

        @Test
        void testUpdateWithNullId() {
            User expectedUser = userWithTestValues();
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.update(expectedUser));
            assertEquals("Id must not be empty", e.getMessage());
        }

        @Test
        void testUpdateWithUpdatedDate() {
            User expectedUser = userWithTestValues();
            expectedUser.setId(randomAlphanumeric(10));
            expectedUser.setUpdatedDate(23432L);
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.update(expectedUser));
            assertEquals("Updated Date must be null", e.getMessage());
        }

        @Test
        void testUpdateWithInvalidPhone() {
            User expectedUser = userWithTestValues();
            expectedUser.setId(randomAlphanumeric(10));
            expectedUser.setPhone(randomAlphabetic(10));
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.update(expectedUser));
            assertEquals("Phone number has to be numeric value only", e.getMessage());
        }

        @Test
        void testUpdateWithNonExistentUser() {
            User expectedUser = userWithTestValues();
            expectedUser.setId(randomAlphanumeric(10));
            expectedUser.setPhone(null);

            when(repository.existsById(eq(expectedUser.getId()))).thenReturn(Mono.just(false));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.update(expectedUser))
                    .expectSubscription()
                    .consumeErrorWith(ex -> {
                        assertTrue(ex instanceof NotFoundException);
                        assertEquals("User does not exist in database", ex.getMessage());
                    })
                    .verify();
        }

        @Test
        void testUpdateWithUsedPhoneNumber() {
            User expectedUser = userWithTestValues();
            expectedUser.setId(randomAlphanumeric(10));

            when(repository.existsById(eq(expectedUser.getId()))).thenReturn(Mono.just(true));
            when(repository.findById(eq(expectedUser.getId()))).thenReturn(Mono.just(expectedUser));

            // respond with other user for which ids will not match and hence it will look like phone number is already
            // registered with other user
            when(repository.findByPhone(eq(expectedUser.getPhone()))).thenReturn(Mono.just(userWithAllValues()));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.update(expectedUser))
                    .expectSubscription()
                    .consumeErrorWith(ex -> {
                        assertTrue(ex instanceof IllegalArgumentException);
                        assertEquals("Other User from our records is registered with the same phone number", ex.getMessage());
                    })
                    .verify();
        }
    }

    @Nested
    @DisplayName("User Delete Tests")
    class DeleteTests {

        @Test
        void testDeleteById() {
            String id = randomAlphanumeric(10);
            when(repository.existsById(eq(id))).thenReturn(Mono.just(true));
            when(repository.deleteById(eq(id))).thenReturn(Mono.empty());

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.deleteById(id))
                    .expectSubscription()
                    .verifyComplete();
        }

        @Test
        void testGetByIdNullString() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                    userService.deleteById(null));
            assertEquals("Id must not be blank or null", e.getMessage());

            e = assertThrows(IllegalArgumentException.class, () ->
                    userService.deleteById(""));
            assertEquals("Id must not be blank or null", e.getMessage());
        }

        @Test
        void testGetByNonExistentId() {
            String id = randomAlphanumeric(10);
            when(repository.existsById(eq(id))).thenReturn(Mono.just(false));

            // Exercise Endpoint
            // Assertions
            StepVerifier.create(userService.deleteById(id))
                    .expectSubscription()
                    .consumeErrorWith(ex -> {
                        assertTrue(ex instanceof NotFoundException);
                        assertEquals("User does not exist in database", ex.getMessage());
                    })
                    .verify();
        }
    }
}
