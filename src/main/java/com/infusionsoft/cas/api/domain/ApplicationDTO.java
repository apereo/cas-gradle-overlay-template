package com.infusionsoft.cas.api.domain;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.infusionsoft.cas.domain.Authority;
import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.domain.UserAccount;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a application
 */
@JsonTypeName("Application")
public class ApplicationDTO {
    private long id;
    private String name;
    private UUID uuid;

    public ApplicationDTO() {
        // For de-serialization
    }

    public ApplicationDTO(long id, String name, UUID uuid) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
