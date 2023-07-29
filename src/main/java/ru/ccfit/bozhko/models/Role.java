package ru.ccfit.bozhko.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    BANKER,
    ACCOUNTANT,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
