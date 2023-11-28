package com.example.mylibrary.service.impl;

import com.example.mylibrary.exceptions.NotAllowedException;
import com.example.mylibrary.exceptions.ObjectNotFoundException;
import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.PostMessageDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.service.MessageService;
import com.example.mylibrary.utils.TestUserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MessageServiceImplTestIT {

    @Autowired
    private MessageService serviceToTest;

    @Autowired
    private MessageRepository messageRepository;


    @Autowired
    private TestUserData testUserData;

    private User user;

    private User admin;

    private Message openMessage1;

    private Message openMessage2;

    private Message openMessage3;

    private Message closedMessage;

    @BeforeEach
    void setUp() {
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
    void tearDown() {
        this.messageRepository.deleteAll();
        testUserData.cleanUp();
    }

    @Test
    void testGetAllOpenMessages() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(closedMessage);

        List<MessageDTO> openMessages = this.serviceToTest.getOpenMessages();

        assertEquals(2,openMessages.size());
        assertEquals("title1",openMessages.get(0).getTitle());
        assertEquals("question1",openMessages.get(0).getQuestion());
        assertEquals("title2",openMessages.get(1).getTitle());
        assertEquals("question2",openMessages.get(1).getQuestion());
    }

    @Test
    void testRegisterMessage() {
        this.serviceToTest.registerMessage(new PostMessageDTO("title","question"),user.getEmail());

        List<Message> messages = this.messageRepository.findAll();

        assertEquals(1,messages.size());
        assertEquals("title",messages.get(0).getTitle());
        assertEquals("question",messages.get(0).getQuestion());
        assertEquals(user,messages.get(0).getUser());
    }

    @Test
    void testGetUsersMessages() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);

        List<MessageDTO> messages = this.serviceToTest.getUsersMessages(user.getEmail());

        assertEquals(2,messages.size());
        assertEquals("title1",messages.get(0).getTitle());
        assertEquals("question1",messages.get(0).getQuestion());
        assertEquals("title2",messages.get(1).getTitle());
        assertEquals("question2",messages.get(1).getQuestion());

    }

    @Test
    void testGetMessage() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);

        Message message = this.serviceToTest.getMessage(openMessage2.getId());

        assertEquals(openMessage2.getId(),message.getId());
        assertEquals("title2",message.getTitle());
        assertEquals("question2",message.getQuestion());
        assertEquals(user,message.getUser());
    }

    @Test
    void testGetMessageWithNotExistingId() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);

        assertThrows(ObjectNotFoundException.class, () -> {
            this.serviceToTest.getMessage(100L);
        });
        try {
            this.serviceToTest.getMessage(100L);
        } catch (ObjectNotFoundException exception) {
            assertEquals("message not found", exception.getMessage());
        }
    }

    @Test
    void testAnswerMessage() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);

        this.serviceToTest.answerMessage(openMessage2.getId(),"response",admin);

        Message message = this.messageRepository.findById(openMessage2.getId()).get();

        assertEquals("response",message.getResponse());
        assertEquals(admin,message.getAdmin());
        assertTrue(message.isClosed());
    }

    @Test
    void testDeleteMessage() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);

        this.serviceToTest.deleteMessage(openMessage2.getId(),user.getEmail());

        List<Message> messages = this.messageRepository.findAll();

        assertEquals(2,messages.size());
        assertEquals(openMessage1.getId(),messages.get(0).getId());
        assertEquals(openMessage3.getId(),messages.get(1).getId());
    }

    @Test
    void testDeleteMessageWithNotExistingId() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);

        assertThrows(ObjectNotFoundException.class, () -> {
            this.serviceToTest.deleteMessage(100L, user.getEmail());
        });
        try {
            this.serviceToTest.deleteMessage(100L, user.getEmail());
        } catch (ObjectNotFoundException exception) {
            assertEquals("message not found", exception.getMessage());
        }
    }

    @Test
    void testDeleteMessageWhenMessageIsSentFromAnother() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);

        assertThrows(NotAllowedException.class, () -> {
            this.serviceToTest.deleteMessage(openMessage1.getId(), admin.getEmail());
        });

    }

    @Test
    @Transactional
    void testDeleteUserMessages() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(openMessage3);
        this.messageRepository.save(closedMessage);

        long count = this.messageRepository.count();

        assertEquals(4,count);

        this.serviceToTest.deleteUserMessages(user.getId());

        List<Message> messages = this.messageRepository.findAll();

        assertEquals(1,messages.size());
        assertEquals(openMessage3.getId(),messages.get(0).getId());
    }



}