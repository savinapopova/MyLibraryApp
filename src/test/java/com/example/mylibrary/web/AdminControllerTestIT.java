package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.UserDTO;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserData testUserData;

    @Autowired
    private ModelMapper modelMapper;

    private User user;

    private User admin;

    @BeforeEach
    public void setUp() {
        testUserData.cleanUp();

        user = testUserData.createTestUser();
        admin = testUserData.createTestAdmin();
    }

    @AfterEach
    public void tearDown() {
        testUserData.cleanUp();
    }

    @Test
    void testUsersWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testUsersWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/users"))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testUsersWhenAdmin() throws Exception {

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setRoles("USER");

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("allUsers", hasSize(1)))
                .andExpect(model().attribute("allUsers", List.of(userDTO)))
                .andExpect(model().attribute("allUsers", contains(hasProperty("firstName", is("userFirstName")))))
                .andExpect(view().name("people"))
                .andExpect(forwardedUrl(null));

    }



}