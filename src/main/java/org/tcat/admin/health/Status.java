package org.tcat.admin.health;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author lin
 * @date 2018/12/4
 */
@JsonInclude(Include.NON_EMPTY)
public final class Status {

    /**
     * {@link Status} indicating that the component or subsystem is in an unknown state.
     */
    public static final Status UNKNOWN = new Status("UNKNOWN");

    /**
     * {@link Status} indicating that the component or subsystem is functioning as
     * expected.
     */
    public static final Status UP = new Status("UP");

    /**
     * {@link Status} indicating that the component or subsystem has suffered an
     * unexpected failure.
     */
    public static final Status DOWN = new Status("DOWN");

    /**
     * {@link Status} indicating that the component or subsystem has been taken out of
     * service and should not be used.
     */
    public static final Status OUT_OF_SERVICE = new Status("OUT_OF_SERVICE");

    private final String code;

    private final String description;

    /**
     * Create a new {@link Status} instance with the given code and an empty description.
     * @param code the status code
     */
    public Status(String code) {
        this(code, "");
    }

    /**
     * Create a new {@link Status} instance with the given code and description.
     * @param code the status code
     * @param description a description of the status
     */
    public Status(String code, String description) {
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(description, "Description must not be null");
        this.code = code;
        this.description = description;
    }

    /**
     * Return the code for this status.
     * @return the code
     */
    @JsonProperty("status")
    public String getCode() {
        return this.code;
    }

    /**
     * Return the description of this status.
     * @return the description
     */
    @JsonInclude(Include.NON_EMPTY)
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj instanceof Status) {
            return ObjectUtils.nullSafeEquals(this.code, ((Status) obj).code);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public String toString() {
        return this.code;
    }

}

