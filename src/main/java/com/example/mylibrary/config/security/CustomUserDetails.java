package com.example.mylibrary.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final String firstName;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String firstName) {
        super(username, password, authorities);
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }
}

