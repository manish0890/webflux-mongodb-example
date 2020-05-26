package com.manish0890.application.document;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;

public abstract class BaseDocument {
    @Id
    private String id;

    private Long createdDate;

    private Long updatedDate;

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
        return object instanceof BaseDocument && (this == object || EqualsBuilder.reflectionEquals(this, object, defaultExcludedFields));
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, true);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Long updatedDate) {
        this.updatedDate = updatedDate;
    }
}