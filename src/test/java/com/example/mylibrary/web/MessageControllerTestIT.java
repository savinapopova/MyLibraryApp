package com.example.mylibrary.web;

import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTestIT {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUserData testUserData;

    private User user;

    private User admin;

    private Message openMessage1;

    private Message openMessage2;

    private Message openMessage3;

    private Message closedMessage;

    @BeforeEach
    public void setUp() {
        this.messageRepository.deleteAll();
        this.testUserData.cleanUp();

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
        this.testUserData.cleanUp();
    }

    @Test
    void testGetMessagesWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/messages"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetMessages() throws Exception {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);
        this.messageRepository.save(closedMessage);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/messages"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("messages"))
                .andExpect(model().attribute("messages",hasSize(3)))
                .andExpect(view().name("messages"))
                .andExpect(redirectedUrl(null));
    }

    @Test
    void testPostMessageWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testPostMessage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/messages/send")
                        .with(csrf())
                        .param("title","title")
                        .param("question","question"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage",true));

        assertEquals(1,this.messageRepository.count());
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testPostMessageWithNoTitle() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/messages/send")
                        .with(csrf())
                        .param("title","")
                        .param("question","question"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages"))
                .andExpect(flash().attributeExists("postMessageDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.postMessageDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.postMessageDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("title"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.postMessageDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("Title cannot be empty!"))
                                )
                        ))));

        assertEquals(0,this.messageRepository.count());
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testPostMessageWithNoQuestion() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/messages/send")
                        .with(csrf())
                        .param("title","title")
                        .param("question",""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages"))
                .andExpect(flash().attributeExists("postMessageDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.postMessageDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.postMessageDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("field", is("question"))
                                )
                        ))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.postMessageDTO",
                        hasProperty("fieldErrors", hasItem(
                                allOf(
                                        hasProperty("defaultMessage", is("Question cannot be empty!"))
                                )
                        ))));

        assertEquals(0,this.messageRepository.count());
    }

    @Test
    void testDeleteMessageWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/messages/delete/{id}",1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteMessage() throws Exception {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);
        this.messageRepository.save(closedMessage);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/messages/delete/{id}",openMessage2.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages"))
        ;

        assertEquals(3,this.messageRepository.count());
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteMessageWhenNotExists() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/messages/delete/{id}", 50L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("object-not-found"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message","message not found"))
        ;


    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService",
            setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteMessageWhenSentFromAnother() throws Exception {

        this.messageRepository.save(openMessage3);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/messages/delete/{id}", openMessage3.getId())
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());
        ;


    }

}