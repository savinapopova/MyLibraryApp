package com.example.mylibrary.validation;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.RoleService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UniqueEmailValidatorTestIT {

    @Autowired
    private UniqueEmailValidator validatorToTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    private User user;
    private User admin;

    @BeforeEach
    public void setUp() {
        this.userRepository.deleteAll();

        Role userRole = this.roleService.findByName(RoleName.USER);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);

        user = new User("userFirstName", "userLastName", "userEmail", "userPassword");
        user.getRoles().add(userRole);
        admin = new User("adminFirstName", "adminLastName", "adminEmail", "adminPassword");
        admin.setRoles(Set.of(userRole, adminRole));
        this.userRepository.save(user);
        this.userRepository.save(admin);
    }

    @AfterEach
    public void tearDown() {
        this.userRepository.deleteAll();
    }

    @Test
    void testIsValid() {
        assertTrue(this.validatorToTest.isValid("newEmail", null));
        assertFalse(this.validatorToTest.isValid("userEmail", null));
    }
}
