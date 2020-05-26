package com.manish0890.application.util;

import com.manish0890.application.mongo.document.User;

import java.util.Date;

import static org.apache.commons.lang3.RandomStringUtils.*;

public class TestUtility {
    private TestUtility() {
    }

    public static User userWithTestValues() {
        User user = new User();
        user.setName(randomAlphabetic(10));
        user.setAddress(randomAlphabetic(10));
        user.setZip(randomNumeric(5));
        user.setPhone(randomNumeric(10));
        return user;
    }

    public static User userWithAllValues() {
        User user = userWithTestValues();
        user.setCreatedDate(new Date().getTime() - 1000);
        user.setUpdatedDate(new Date().getTime());
        user.setId(randomAlphanumeric(20));
        return user;
    }
}
