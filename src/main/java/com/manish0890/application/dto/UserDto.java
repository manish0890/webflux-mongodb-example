package com.manish0890.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends BaseDto {

    @NotBlank(message = "Name must be provided")
    private String name;

    private String address;

    @NotBlank(message = "Zip code must be provided")
    @Size(min = 5, max = 5, message = "Zip code must be 5 digits only")
    private String zip;

    @NotBlank(message = "Phone number must be provided")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits only")
    private String phone;

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
