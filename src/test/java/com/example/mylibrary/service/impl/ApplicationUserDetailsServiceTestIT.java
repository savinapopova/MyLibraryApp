package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.util.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationUserDetailsServiceTestIT {

    @Autowired
    private UserDetailsService serviceToTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleServiceImpl roleService;

    private User user;

    private User admin;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        Role userRole = this.roleService.findByName(RoleName.USER);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);

        user = new User("userFirstName", "userLastName", "userEmail",
                "userPassword");
        user.getRoles().add(userRole);
        admin = new User("adminFirstName", "adminLastName", "adminEmail",
                "adminPassword");
        admin.setRoles(Set.of(userRole, adminRole));
        this.userRepository.save(user);
        this.userRepository.save(admin);

    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testLoadUserByUsername() {
        UserDetails user =  this.serviceToTest.loadUserByUsername("userEmail");
        assertEquals("userEmail", user.getUsername());
        assertEquals("userPassword", user.getPassword());
        assertEquals(1, user.getAuthorities().size());
        assertEquals("ROLE_USER", user.getAuthorities().stream().findFirst().get().getAuthority());



        UserDetails admin = this.serviceToTest.loadUserByUsername("adminEmail");
        assertEquals("adminEmail", admin.getUsername());
        assertEquals("adminPassword", admin.getPassword());
        assertEquals(2, admin.getAuthorities().size());
        assertEquals("ROLE_ADMIN", admin.getAuthorities().stream().findFirst().get().getAuthority());


    }

}