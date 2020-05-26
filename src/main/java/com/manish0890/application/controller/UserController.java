package com.manish0890.application.controller;

import com.manish0890.application.dto.UserDto;
import com.manish0890.application.document.User;
import com.manish0890.application.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.manish0890.application.constants.RequestMappingConstants.*;

@RestController
@RequestMapping(USER)
@Validated
public class UserController {

    private UserService userService;
    private ModelMapper modelMapper;

    @PostMapping
    public Mono<UserDto> create(@Valid @RequestBody UserDto userDto) {
        return userService.create(convertToDocument(userDto))
                .map(this::convertToDto);
    }

    @GetMapping(USER_BY_ID)
    public Mono<UserDto> getById(@PathVariable(value = "id") String id) {
        return userService.getById(id)
                .map(this::convertToDto);
    }

    @GetMapping(USER_GET_BY_PHONE)
    public Mono<UserDto> getByPhoneNumber(@PathVariable("phone") @NotBlank @Size(min = 10, max = 10) String phone) {
        return userService.getByPhoneNumber(phone)
                .map(this::convertToDto);
    }

    @GetMapping(USER_GET_BY_ZIP)
    public Flux<UserDto> getByZipCode(@PathVariable("zip") @NotBlank @Size(min = 5, max = 5) String zip) {
        return userService.getByZip(zip)
                .map(this::convertToDto);
    }

    @GetMapping(USER_GET_ALL)
    public Flux<UserDto> getAll() {
        return userService.getAll()
                .map(this::convertToDto);
    }

    @PutMapping
    public Mono<UserDto> update(@RequestBody UserDto userDto) {
        return userService.update(convertToDocument(userDto))
                .map(this::convertToDto);
    }

    @DeleteMapping(USER_BY_ID)
    public Mono<Void> deleteById(@PathVariable(value = "id") String id) {
        return userService.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertToDocument(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
