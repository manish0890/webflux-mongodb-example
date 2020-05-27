package com.manish0890.application.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.Null;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class BaseDto {

    private String id;

    @Null(message = "Created Date must be null")
    private Date createdDate;

    @Null(message = "Updated Date must be null")
    private Date updatedDate;

    // Fields to exclude during excluded fields Equals method
    private static final List<String> defaultExcludedFields = Arrays.asList("id", "createdDate", "updatedDate");

    /**
     * Perform an exact field for field value match between this object and the provided object.
     *
     * @param object {@link Object}
     * @return boolean
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof BaseDto && (this == object || EqualsBuilder.reflectionEquals(this, object, defaultExcludedFields));
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, true);
    }

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