package com.manish0890.application.dto;

import javax.validation.constraints.Null;
import java.util.Date;

public abstract class BaseDto {

    private String id;
    @Null(message = "Created Date must be null")
    private Date createdDate;

    @Null(message = "Updated Date must be null")
    private Date updatedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}