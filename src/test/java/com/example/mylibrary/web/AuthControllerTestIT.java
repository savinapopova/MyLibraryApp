package com.example.mylibrary.web;

import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUserData testUserData;

    @BeforeEach
    void setUp() {
        this.userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        this.userRepository.deleteAll();
    }

    @Test
    void login() throws Exception {
        testUserData.createTestUser();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(redirectedUrl(null));

    }

    @Test
    void testOnFailedLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login-error")
                        .param("email", "test@example.com")
                        .param("password", "testPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(redirectedUrl(null));

    }

    @Test
    void register() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/register")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(redirectedUrl(null));

    }

    @Test
    void testRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("email", "testEmail@email.com")
                        .param("password", "testPassword")
                        .param("confirmPassword", "testPassword")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        User user = this.userRepository.findByEmail("testEmail@email.com").orElse(null);

        assertNotNull(user);
    }

    @Test
    void testRegisterWithBlankOrShortFirstName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .param("firstName", "")
                        .param("lastName", "testLastName")
                        .param("email", "testEmail@email.com")
                        .param("password", "testPassword")
                        .param("confirmPassword", "testPassword")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("userRegisterDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("firstName"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("First name length must be between 2 and 20 characters!"))
                                )
                        ))));




        User user = this.userRepository.findByEmail("testEmail@email.com").orElse(null);

        assertNull(user);
    }

    @Test
    void testRegisterWithBlankOrShortLastName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .param("firstName", "testFirstName")
                        .param("lastName", "")
                        .param("email", "testEmail@email.com")
                        .param("password", "testPassword")
                        .param("confirmPassword", "testPassword")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("userRegisterDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("lastName"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("Last name length must be between 2 and 20 characters!"))
                                )
                        ))));




        User user = this.userRepository.findByEmail("testEmail@email.com").orElse(null);

        assertNull(user);
    }

    @Test
    void testRegisterWithNotValidEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("email", "testEmail")
                        .param("password", "testPassword")
                        .param("confirmPassword", "testPassword")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("userRegisterDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("email"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("Email must be valid!"))
                                )
                        ))));




        User user = this.userRepository.findByEmail("testEmail").orElse(null);

        assertNull(user);
    }

    @Test
    void testRegisterWithBlankOrShortPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("email", "testEmail@email.com")
                        .param("password", "")
                        .param("confirmPassword", "testPassword")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("userRegisterDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("password"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("Password length must be between 5 and 20 characters!"))
                                )
                        ))));




        User user = this.userRepository.findByEmail("testEmail@email.com").orElse(null);

        assertNull(user);
    }

    @Test
    void testRegisterWithNoMatchingPasswords() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("email", "testEmail@email.com")
                        .param("password", "testPassword")
                        .param("confirmPassword", "testPassword1")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("userRegisterDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("confirmPassword"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("Passwords should match."))
                                )
                        ))));
    }

    @Test
    void testRegisterWithNotUniqueEmail() throws Exception {
        userRepository.save(new User("firstName", "lastName", "testEmail@email.com", "password"));
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .param("firstName", "testFirstName")
                        .param("lastName", "testLastName")
                        .param("email", "testEmail@email.com")
                        .param("password", "testPassword")
                        .param("confirmPassword", "testPassword1")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"))
                .andExpect(flash().attributeExists("userRegisterDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("email"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.userRegisterDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("Email already exists."))
                                )
                        ))));
    }

    }