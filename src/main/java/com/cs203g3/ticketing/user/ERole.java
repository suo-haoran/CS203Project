package com.cs203g3.ticketing.user;

public enum ERole {
    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    public final String name;
    ERole(String name) {
        this.name = name;
    }
}
