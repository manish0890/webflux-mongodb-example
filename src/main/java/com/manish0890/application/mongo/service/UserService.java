package com.manish0890.application.mongo.service;

import com.manish0890.application.exception.NotFoundException;
import com.manish0890.application.exception.ServiceException;
import com.manish0890.application.mongo.document.User;
import com.manish0890.application.mongo.repository.UserReactiveRepository;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@Service
public class UserService {

    private final UserReactiveRepository userReactiveRepository;

    private final BeanUtilsBean beanUtils = new BeanUtilsBean();

    private static final String NOT_FOUND_MSG = "User does not exist in database";

    @Autowired
    public UserService(UserReactiveRepository userReactiveRepository) {
        this.userReactiveRepository = userReactiveRepository;
    }

    public Mono<User> create(User user) {
        Assert.notNull(user, "User cannot be null");
        Assert.isTrue(StringUtils.isEmpty(user.getId()), "Id must be empty");
        Assert.isNull(user.getCreatedDate(), "Created Date must be null");
        Assert.isNull(user.getUpdatedDate(), "Updated Date must be null");
        Assert.isTrue(StringUtils.isNotBlank(user.getPhone()), "Phone number must be provided");
        Assert.isTrue(StringUtils.isNumeric(user.getPhone()), "Phone number has to be numeric value only");

        long createdDate = new Date().getTime();
        user.setCreatedDate(createdDate);

        // Verify if phone number is already registered with other user in database
        return userReactiveRepository.findByPhone(user.getPhone())
                // If phone number is unique then create a new document
                .switchIfEmpty(userReactiveRepository.save(user))
                .flatMap(returnedUser -> createdDate != returnedUser.getCreatedDate() ?
                        // Created date does not match with returned user, means new user was not created
                        Mono.error(new IllegalArgumentException("Other User from our records is registered with the same phone number")) :
                        // created date matches hence this is newly created user
                        Mono.just(returnedUser));
    }

    public Mono<User> getById(String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "Id must not be blank or null");

        return userReactiveRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_MSG)));
    }

    public Mono<User> getByPhoneNumber(String phone) {
        Assert.isTrue(StringUtils.isNotBlank(phone), "Phone must not be blank or null");

        return userReactiveRepository.findByPhone(phone)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_MSG)));
    }

    public Flux<User> getByZip(String zip) {
        Assert.isTrue(StringUtils.isNotBlank(zip), "Zip must not be blank or null");

        return userReactiveRepository.findByZipOrderByCreatedDateDesc(zip);
    }

    public Flux<User> getAll() {
        return userReactiveRepository.findAll(Sort.by("createdDate").descending());
    }

    public Mono<User> update(User userToUpdate) {
        Assert.notNull(userToUpdate, "User cannot be null");
        Assert.isTrue(StringUtils.isNotBlank(userToUpdate.getId()), "Id must not be empty");
        Assert.isNull(userToUpdate.getUpdatedDate(), "Updated Date must be null");

        if (StringUtils.isNotBlank(userToUpdate.getPhone())) {
            Assert.isTrue(StringUtils.isNumeric(userToUpdate.getPhone()), "Phone number has to be numeric value only");
        }

        userToUpdate.setUpdatedDate(new Date().getTime());

        // Find if userToUpdate with provided id exists
        return userReactiveRepository.existsById(userToUpdate.getId())
                .flatMap(exists -> BooleanUtils.isFalse(exists) ?
                        // Throw NotFoundException if record does not exist
                        Mono.error(new NotFoundException(NOT_FOUND_MSG)) :
                        // Fetch userToUpdate with id
                        userReactiveRepository.findById(userToUpdate.getId()))
                .flatMap(existingUser -> {
                    // If phone number is provided in request then make sure that phone number is not
                    // tied to other userToUpdate in database
                    if (StringUtils.isNotBlank(userToUpdate.getPhone())) {
                        return userReactiveRepository.findByPhone(userToUpdate.getPhone())
                                .flatMap(userByPhone -> !userByPhone.getId().equals(userToUpdate.getId()) ?
                                        Mono.error(new IllegalArgumentException("Other User from our records is registered with the same phone number")) :
                                        Mono.just(userByPhone))
                                .defaultIfEmpty(existingUser);
                    } else {
                        return Mono.just(existingUser);
                    }
                })
                .flatMap(existingUser -> {
                    // Copy non null values from existing record to new userToUpdate object
                    copyProperties(existingUser, userToUpdate);

                    // Finally save the userToUpdate database
                    return userReactiveRepository.save(existingUser);
                });
    }

    public Mono<Void> deleteById(String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "Id must not be blank or null");

        return userReactiveRepository.existsById(id)
                .flatMap(exists -> BooleanUtils.isFalse(exists) ?
                        Mono.error(new NotFoundException(NOT_FOUND_MSG)) :
                        userReactiveRepository.deleteById(id));
    }

    private void copyProperties(User destination, User source) {
        try {
            beanUtils.copyProperties(destination, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
