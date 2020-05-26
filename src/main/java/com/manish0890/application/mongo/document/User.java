package com.manish0890.application.mongo.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User extends BaseDocument {

    private String name;
    private String address;
    private String zip;
    private String phone;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}