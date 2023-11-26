package com.example.mylibrary.service.impl;

import com.example.mylibrary.errors.ObjectNotFoundException;
import com.example.mylibrary.model.dto.UserRegisterDTO;
import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.RoleService;
import com.example.mylibrary.service.UserService;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTestIT {

    @Autowired
    private UserService serviceToTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUserData testUserData;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    private User admin;

    @BeforeEach
    void setUp() {
        testUserData.cleanUp();

        user = testUserData.createTestUser();
        admin = testUserData.createTestAdmin();
    }

    @AfterEach
    void tearDown() {
        testUserData.cleanUp();
    }

    @Test
    void testRegisterUser() {
        UserRegisterDTO registerDTO =
                new UserRegisterDTO("email", "firstName", "lastName",
                        "password", "password");

        assertEquals(2, this.userRepository.count());

        this.serviceToTest.register(registerDTO);

        User user = this.userRepository.findByEmail("email").orElse(null);
        List<User> users = this.userRepository.findAll();
        assertNotNull(user);
        assertEquals(3, users.size());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertTrue(passwordEncoder.matches("password", user.getPassword()));
        assertEquals(1, user.getRoles().size());
        assertEquals("USER", user.getRoles().stream().findFirst().get().getName().name());

    }

    @Test
    void testGetUserByUsername() {
        User user = this.serviceToTest.getUser("userEmail");
        assertEquals("userEmail", user.getEmail());
        assertEquals("userFirstName", user.getFirstName());
        assertEquals("userLastName", user.getLastName());
        assertEquals("userPassword", user.getPassword());
        assertEquals(1, user.getRoles().size());
        assertEquals("USER", user.getRoles().stream().findFirst().get().getName().name());
    }

    @Test
    void testGetByUsernameNotExisting() {

        assertThrows(ObjectNotFoundException.class,
                () -> this.serviceToTest.getUser("notExistingEmail"));
        try {
            this.serviceToTest.getUser("notExistingEmail");
        } catch (ObjectNotFoundException exception) {
            assertEquals("user with email notExistingEmail not found",
                    exception.getMessage());
        }
    }

    @Test
    void testGetUserById() {
        User userFromDB = this.serviceToTest.getUser(user.getId());
        assertEquals("userEmail", user.getEmail());
        assertEquals("userFirstName", user.getFirstName());
        assertEquals("userLastName", user.getLastName());
        assertEquals("userPassword", user.getPassword());
        assertEquals(1, user.getRoles().size());
        assertEquals("USER", user.getRoles().stream().findFirst().get().getName().name());
    }

    @Test
    void testGetUserByIdNotExisting() {

        assertThrows(ObjectNotFoundException.class,
                () -> this.serviceToTest.getUser(100L));
        try {
            this.serviceToTest.getUser(100L);
        } catch (ObjectNotFoundException exception) {
            assertEquals("user with id 100 not found",
                    exception.getMessage());
        }
    }

    @Test
    void testSaveUser() {
        User user = this.serviceToTest.getUser("userEmail");
        user.setFirstName("newFirstName");
        user.setLastName("newLastName");
        user.setPassword(passwordEncoder.encode("newPassword"));
        this.serviceToTest.saveUser(user);

        User userFromDb = this.serviceToTest.getUser("userEmail");
        assertEquals("newFirstName", userFromDb.getFirstName());
        assertEquals("newLastName", userFromDb.getLastName());
        assertTrue(passwordEncoder.matches("newPassword", userFromDb.getPassword()));
    }

    @Test
    void testFindAllUsers() {
        List<User> users = this.serviceToTest.findAllUsers();
        assertEquals(2, users.size());
        assertEquals("userEmail", users.get(0).getEmail());
        assertEquals("adminEmail", users.get(1).getEmail());
    }

    @Test
    void testDeleteUser() {
        this.serviceToTest.deleteUser(7L);
        List<User> users = this.serviceToTest.findAllUsers();
        assertEquals(2, users.size());
        assertEquals("userEmail", users.get(0).getEmail());
        assertEquals("adminEmail", users.get(1).getEmail());
    }




}