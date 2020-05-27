package com.manish0890.application.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manish0890.application.constants.RequestMappingConstants;
import com.manish0890.application.document.User;
import com.manish0890.application.dto.ErrorSummary;
import com.manish0890.application.dto.UserDto;
import com.manish0890.application.exception.NotFoundException;
import com.manish0890.application.repository.UserReactiveRepository;
import com.manish0890.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.manish0890.application.util.TestUtility.userWithTestValues;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserController.class)
public class UserControllerIntegrationTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserReactiveRepository repository;

    @Autowired
    private WebTestClient webTestClient;

    private final ModelMapper modelMapper = new ModelMapper();
    private User expectedUser;
    private UserDto expectedUserDto;

    @BeforeEach
    void setup() {
        expectedUser = userWithTestValues();
        expectedUserDto = modelMapper.map(expectedUser, UserDto.class);
    }

    @Nested
    @DisplayName("User Controller UserDto validation Tests")
    class CreateTests {

        @Test
        void testCreate() {
            doReturn(Mono.just(expectedUser)).when(userService).create(eq(expectedUser));

            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .consumeWith(body -> {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            assertEquals(expectedUserDto, mapper.readValue(body.getResponseBody(), UserDto.class));
                        } catch (IOException e) {
                            throw new AssertionError("Object mapper threw error", e);
                        }
                    });

        }

        @Test
        void testCreateWithoutName() {
            expectedUserDto.setName(null);

            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Name must be provided")));
        }

        @Test
        void testCreateWithoutZip() {
            expectedUserDto.setZip(null);

            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Zip code must be provided")));
        }

        @Test
        void testCreateInvalidZip() {
            expectedUserDto.setZip(randomNumeric(6));
            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Zip code must be 5 digits only")));


            expectedUserDto.setZip(randomNumeric(4));
            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Zip code must be 5 digits only")));

        }

        @Test
        void testCreateWithoutPhone() {
            expectedUserDto.setPhone(null);
            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Phone number must be provided")));
        }

        @Test
        void testCreateInvalidPhone() {
            expectedUserDto.setPhone(randomNumeric(9));
            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Phone number must be 10 digits only")));

            expectedUserDto.setPhone(randomNumeric(11));
            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Phone number must be 10 digits only")));

        }

        @Test
        void testCreateWithCreatedDate() {
            expectedUserDto.setCreatedDate(new Date());

            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Created Date must be null")));
        }

        @Test
        void testCreateWithUpdatedDate() {
            expectedUserDto.setUpdatedDate(new Date());

            webTestClient.post()
                    .uri(RequestMappingConstants.USER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(expectedUserDto))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("Updated Date must be null")));
        }
    }

    @Test
    void testGetById() {
        String id = randomAlphabetic(10);
        doReturn(Mono.just(expectedUser)).when(userService).getById(eq(id));

        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(body -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        assertEquals(expectedUserDto, mapper.readValue(body.getResponseBody(), UserDto.class));
                    } catch (IOException e) {
                        throw new AssertionError("Object mapper threw error", e);
                    }
                });
    }

    @Test
    void testGetByIdHandles404() {
        String id = randomAlphabetic(10);
        doReturn(Mono.error(new NotFoundException("xyz"))).when(userService).getById(eq(id));

        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .consumeWith(body -> assertEquals("xyz", getErrorMessage(body, ErrorSummary.class)));
    }

    @Test
    void testGetByPhone() {
        doReturn(Mono.just(expectedUser)).when(userService).getByPhoneNumber(eq(expectedUserDto.getPhone()));

        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/phone/" + expectedUserDto.getPhone())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(body -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        assertEquals(expectedUserDto, mapper.readValue(body.getResponseBody(), UserDto.class));
                    } catch (IOException e) {
                        throw new AssertionError("Object mapper threw error", e);
                    }
                });
    }

    @Test
    void testGetByPhoneHandles412() {
        doReturn(Mono.error(new IllegalArgumentException("xyz"))).when(userService).getByPhoneNumber(eq(expectedUserDto.getPhone()));

        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/phone/" + expectedUserDto.getPhone())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(412)
                .expectBody()
                .consumeWith(body -> assertEquals("xyz", getErrorMessage(body, ErrorSummary.class)));
    }

    @Test
    void testGetByPhoneInvalidPhone() {
        expectedUserDto.setPhone(randomNumeric(9));
        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/phone/" + expectedUserDto.getPhone())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("size must be between 10 and 10")));

        expectedUserDto.setPhone(randomNumeric(11));
        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/phone/" + expectedUserDto.getPhone())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("size must be between 10 and 10")));
    }

    @Test
    void testGetByZip() {
        doReturn(Flux.just(expectedUser)).when(userService).getByZip(eq(expectedUserDto.getZip()));

        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/zip/" + expectedUserDto.getZip())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(body -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        assertEquals(Collections.singletonList(expectedUserDto), mapper.readValue(body.getResponseBody(), new TypeReference<ArrayList<UserDto>>() {
                        }));
                    } catch (IOException e) {
                        throw new AssertionError("Object mapper threw error", e);
                    }
                });
    }

    @Test
    void testGetByZipHandlesException() {
        doReturn(Flux.error(new UnsupportedOperationException("xyz"))).when(userService).getByZip(eq(expectedUserDto.getZip()));

        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/zip/" + expectedUserDto.getZip())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody()
                .consumeWith(body -> assertEquals("xyz", getErrorMessage(body, ErrorSummary.class)));
    }

    @Test
    void testGetByZipInvalidZip() {
        expectedUserDto.setZip(randomNumeric(6));
        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/zip/" + expectedUserDto.getZip())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("size must be between 5 and 5")));

        expectedUserDto.setZip(randomNumeric(4));
        webTestClient.get()
                .uri(RequestMappingConstants.USER + "/zip/" + expectedUserDto.getZip())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(body -> assertTrue(getErrorMessage(body, ErrorSummary.class).contains("size must be between 5 and 5")));
    }

    @Test
    void testGetAll() {
        doReturn(Flux.just(expectedUser)).when(userService).getAll();

        webTestClient.get()
                .uri(RequestMappingConstants.USER + RequestMappingConstants.USER_GET_ALL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(body -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        assertEquals(Collections.singletonList(expectedUserDto), mapper.readValue(body.getResponseBody(), new TypeReference<ArrayList<UserDto>>() {
                        }));
                    } catch (IOException e) {
                        throw new AssertionError("Object mapper threw error", e);
                    }
                });
    }

    @Test
    void testUpdate() {
        doReturn(Mono.just(expectedUser)).when(userService).update(eq(expectedUser));

        webTestClient.put()
                .uri(RequestMappingConstants.USER)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(expectedUserDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(body -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        assertEquals(expectedUserDto, mapper.readValue(body.getResponseBody(), UserDto.class));
                    } catch (IOException e) {
                        throw new AssertionError("Object mapper threw error", e);
                    }
                });
    }

    private String getErrorMessage(EntityExchangeResult<byte[]> body, Class T) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(body.getResponseBody(), ErrorSummary.class).getMessage();
        } catch (IOException e) {
            throw new AssertionError("Object mapper threw error", e);
        }
    }

}
