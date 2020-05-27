package com.manish0890.application.controller;

import com.manish0890.application.document.User;
import com.manish0890.application.dto.UserDto;
import com.manish0890.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.manish0890.application.util.TestUtility.userWithTestValues;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerUnitTest {

    @Mock
    private UserService userService;

    private final ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UserController userController;

    private User expectedUser;
    private UserDto expectedUserDto;

    @BeforeEach
    private void Setup() {
        expectedUser = userWithTestValues();
        expectedUserDto = modelMapper.map(expectedUser, UserDto.class);
    }

    @Test
    void testCreate() {
        when(userService.create(eq(expectedUser))).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(userController.create(expectedUserDto))
                .expectSubscription()
                .expectNext(expectedUserDto)
                .verifyComplete();
    }

    @Test
    void testGetById() {
        when(userService.getById(eq(expectedUser.getId()))).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(userController.getById(expectedUser.getId()))
                .expectSubscription()
                .expectNext(expectedUserDto)
                .verifyComplete();
    }

    @Test
    void testGetByPhone() {
        when(userService.getByPhoneNumber(eq(expectedUser.getPhone()))).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(userController.getByPhoneNumber(expectedUser.getPhone()))
                .expectSubscription()
                .expectNext(expectedUserDto)
                .verifyComplete();
    }

    @Test
    void testGetByZip() {
        when(userService.getByZip(eq(expectedUser.getZip()))).thenReturn(Flux.just(expectedUser));

        StepVerifier.create(userController.getByZipCode(expectedUser.getZip()))
                .expectSubscription()
                .expectNext(expectedUserDto)
                .verifyComplete();
    }

    @Test
    void testGetAll() {
        when(userService.getAll()).thenReturn(Flux.just(expectedUser));

        StepVerifier.create(userController.getAll())
                .expectSubscription()
                .expectNext(expectedUserDto)
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        when(userService.update(eq(expectedUser))).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(userController.update(expectedUserDto))
                .expectSubscription()
                .expectNext(expectedUserDto)
                .verifyComplete();
    }

    @Test
    void testDeleteById() {
        when(userService.deleteById(eq(expectedUser.getId()))).thenReturn(Mono.empty());

        StepVerifier.create(userController.deleteById(expectedUser.getId()))
                .expectSubscription()
                .verifyComplete();
    }
}
