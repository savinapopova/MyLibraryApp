package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.util.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ApplicationUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(ApplicationUserDetailsService::map)
                .orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found!"));
    }

    private static CustomUserDetails map(User userEntity) {
        return new CustomUserDetails(
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRoles().stream().map(ApplicationUserDetailsService::map).toList(),
                userEntity.getFirstName());

    }

    private static GrantedAuthority map(Role userRoleEntity) {
        return new SimpleGrantedAuthority(
                "ROLE_" + userRoleEntity.getName().name()
        );
    }
}