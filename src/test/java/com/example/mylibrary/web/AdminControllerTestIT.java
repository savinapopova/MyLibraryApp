package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.UserDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.MessageRepository;
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
    private MessageRepository messageRepository;

    @Autowired
    private ModelMapper modelMapper;

    private User user;

    private User admin;

    private Message openMessage1;

    private Message openMessage2;

    private Message openMessage3;

    private Message closedMessage;


    @BeforeEach
    public void setUp() {
        this.messageRepository.deleteAll();
        testUserData.cleanUp();

        user = testUserData.createTestUser();
        admin = testUserData.createTestAdmin();

        openMessage1 = new Message("title1","question1");
        openMessage1.setUser(user);
        openMessage2 = new Message("title2","question2");
        openMessage2.setUser(user);
        openMessage3 = new Message("title3","question3");
        openMessage3.setUser(admin);
        closedMessage = new Message("title4","question4");
        closedMessage.setUser(user);
        closedMessage.setClosed(true);
        closedMessage.setAdmin(admin);
    }

    @AfterEach
    public void tearDown() {
        this.messageRepository.deleteAll();
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

    @Test
    void testGetOpenMessagesWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/messages"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetOpenMessagesWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/messages"))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetOpenMessagesWhenAdmin() throws Exception {


        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(closedMessage);


        List<Message> messages = this.messageRepository.findAllByClosed(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/messages"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("messages", hasSize(2)))
                .andExpect(model().attribute("messages", List.of(modelMapper.map(messages, MessageDTO[].class))))
                .andExpect(model().attribute("messages", hasItem(
                        allOf(
                                hasProperty("title", is("title1"))

                        )
                )))
                .andExpect(model().attribute("messages", hasItem(
                        allOf(
                                hasProperty("title", is("title2"))

                        ))))
                .andExpect(view().name("messages-admin"))
                .andExpect(forwardedUrl(null));

    }



}