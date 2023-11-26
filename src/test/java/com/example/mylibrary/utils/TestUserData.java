package com.example.mylibrary.utils;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TestUserData {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    public User createTestUser() {
        Role userRole = this.roleService.findByName(RoleName.USER);


       User user = new User("userFirstName", "userLastName", "userEmail",
                "userPassword");
        user.getRoles().add(userRole);
        this.userRepository.save(user);
        return user;
    }

    public User createTestAdmin() {
        Role userRole = this.roleService.findByName(RoleName.USER);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);

       User admin = new User("adminFirstName", "adminLastName", "adminEmail",
                "adminPassword");
        admin.setRoles(Set.of(userRole, adminRole));
        this.userRepository.save(admin);

        return admin;
    }

    public void cleanUp() {
        this.userRepository.deleteAll();
    }
}
