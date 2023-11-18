package com.example.mylibrary.service.impl;

import com.example.mylibrary.errors.NotAllowedException;
import com.example.mylibrary.errors.ObjectNotFoundException;
import com.example.mylibrary.model.dto.AddBookDTO;
import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;
import com.example.mylibrary.model.dto.UserDTO;
import com.example.mylibrary.model.entity.*;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminServiceImplTestIT {

    @Autowired
    private AdminService serviceToTest;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CategoryService categoryService;

    private Message openMessage1;

    private Message openMessage2;

    private Message closedMessage;

    private User user;

    private User admin;

    private Book book1;

    private Book book2;

    private Book book3;

    private Book book4;


    private Checkout checkout1;


    private Category biography;

    private Category cookbook;

    @BeforeEach
    void setUp() {
        this.messageRepository.deleteAll();
        this.userRepository.deleteAll();
        this.bookRepository.deleteAll();

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

        openMessage1 = new Message("title1","question1");
        openMessage1.setUser(user);
        openMessage2 = new Message("title2","question2");
        openMessage2.setUser(user);
        closedMessage = new Message("title3","question3");
        closedMessage.setUser(user);
        closedMessage.setClosed(true);
        closedMessage.setAdmin(admin);

        biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);

        book1 = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        book2 = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 2, cookbook);
        book3 = new Book(3L, "title3", "author3",
                "image3", "description3", 0, 0, cookbook);
        book4 = new Book(4L, "title4", "author4",
                "image4", "description4", 2, 0, cookbook);


        checkout1 = new Checkout(book1, user);



    }

    @AfterEach
    void tearDown() {
        this.messageRepository.deleteAll();
        this.userRepository.deleteAll();
        this.bookRepository.deleteAll();
    }

    @Test
    void testGetOpenMessages() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(closedMessage);

        List<MessageDTO> openMessages = this.serviceToTest.getOpenMessages();

        assertEquals(2, openMessages.size());
        assertEquals (openMessages.get(0).getTitle(),openMessage1.getTitle());
        assertEquals(openMessages.get(1).getTitle(),openMessage2.getTitle());
        assertEquals(openMessages.get(0).getClass(),MessageDTO.class);
    }

    @Test
    void testSendResponse() {
        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(closedMessage);

        List<MessageDTO> openMessages = this.serviceToTest.getOpenMessages();

        assertEquals(openMessages.size(), 2);
        assertEquals(openMessages.get(0).getTitle(), openMessage1.getTitle());

        MessageResponseDTO messageResponseDTO = new MessageResponseDTO("response");
        this.serviceToTest.sendResponse(openMessage1.getId(), messageResponseDTO, admin.getEmail());

        openMessages = this.serviceToTest.getOpenMessages();
        Message answeredMessage = this.messageRepository.findById(openMessage1.getId()).get();

        assertEquals(openMessages.size(), 1);
        assertTrue(answeredMessage.isClosed());
        assertEquals(answeredMessage.getAdmin().getId(), admin.getId());


    }

    @Test
    void testPostBookDTO() {

        long count = this.bookRepository.count();

        assertEquals(0, count);

        AddBookDTO addBookDTO = new AddBookDTO( "title3", "author3",
                "description3","FANTASY","image3", 3 );
        this.serviceToTest.postBook(addBookDTO);

        List<Book> books = this.bookRepository.findAll();
        assertEquals(1, books.size());
        assertEquals(books.get(0).getTitle(), addBookDTO.getTitle());
    }

    @Test
    void testIncreaseBookQuantity() {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        this.serviceToTest.increaseBookQuantity(book1.getId());
        Book book = this.bookRepository.findById(book1.getId()).get();

        assertEquals(book.getCopies(), 2);
        assertEquals(book.getCopiesAvailable(), 2);

        this.serviceToTest.increaseBookQuantity(book1.getId());
        book = this.bookRepository.findById(book1.getId()).get();

        assertEquals(book.getCopies(), 3);
        assertEquals(book.getCopiesAvailable(), 3);
    }

    @Test
    void testDecreaseBookQuantity() {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        assertEquals(book2.getCopies(), 2);
        assertEquals(book2.getCopiesAvailable(), 2);



        this.serviceToTest.decreaseBookQuantity(book2.getId());
      Book  book = this.bookRepository.findById(book2.getId()).get();

        assertEquals(book.getCopies(), 1);
        assertEquals(book.getCopiesAvailable(), 1);
    }

    @Test
    void testDecreaseBookQuantityWhenZeroCopies() {

        this.bookRepository.save(book3);

        assertEquals(book3.getCopies(), 0);
        assertEquals(book3.getCopiesAvailable(), 0);

        this.serviceToTest.decreaseBookQuantity(book3.getId());
        Book  book = this.bookRepository.findById(book3.getId()).get();

        assertEquals(book.getCopies(), 0);
        assertEquals(book.getCopiesAvailable(), 0);
    }

    @Test
    void testDecreaseBookQuantityWhenZeroCopiesAvailable() {

        this.bookRepository.save(book4);

        assertEquals(book4.getCopies(), 2);
        assertEquals(book4.getCopiesAvailable(), 0);

        this.serviceToTest.decreaseBookQuantity(book4.getId());
        Book  book = this.bookRepository.findById(book4.getId()).get();

        assertEquals(book.getCopies(), 2);
        assertEquals(book.getCopiesAvailable(), 0);
    }

    @Test
    void testDeleteBook() {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);


        assertEquals(2, this.bookRepository.count());

        this.serviceToTest.deleteBook(book1.getId());

        assertEquals(1, this.bookRepository.count());
        assertEquals(book2.getTitle(), this.bookRepository.findAll().get(0).getTitle());
    }

    @Test
    void testGetAllUsersExceptPrincipal() {
        List<UserDTO> users = this.serviceToTest.getAllUsersExceptPrincipal(admin.getEmail());

        assertEquals(1, users.size());
        assertEquals(users.get(0).getEmail(), user.getEmail());
    }

    @Test
    void testAddAdmin() {

        Set<Role> roles = user.getRoles();

        assertEquals(1, roles.size());
        assertFalse(roles.stream().map(Role::getName)
                .anyMatch(roleName -> roleName.equals(RoleName.ADMIN)));

        this.serviceToTest.addAdmin(user.getId());

        user = this.userRepository.findById(user.getId()).get();
        roles = user.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.stream().map(Role::getName)
                .anyMatch(roleName -> roleName.equals(RoleName.ADMIN)));

    }

    @Test
    void testRemoveAdmin() {
        Set<Role> roles = admin.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.stream().map(Role::getName)
                .anyMatch(roleName -> roleName.equals(RoleName.ADMIN)));

        this.serviceToTest.removeAdmin(admin.getId(), "tesEmail");
       admin = this.userRepository.findById(admin.getId()).get();

         roles = admin.getRoles();

          assertEquals(1, roles.size());
          assertFalse(roles.stream().map(Role::getName)
                 .anyMatch(roleName -> roleName.equals(RoleName.ADMIN)));
    }

    @Test
    void testRemoveAdminWhenLogged() {
        Set<Role> roles = admin.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.stream().map(Role::getName)
                .anyMatch(roleName -> roleName.equals(RoleName.ADMIN)));

        this.serviceToTest.removeAdmin(admin.getId(), admin.getEmail());
        admin = this.userRepository.findById(admin.getId()).get();

        roles = admin.getRoles();

        assertEquals(2, roles.size());
        assertTrue(roles.stream().map(Role::getName)
                .anyMatch(roleName -> roleName.equals(RoleName.ADMIN)));
    }

    @Test
    void testGetUserEmail() {
        String userEmail = this.serviceToTest.getUserEmail(user.getId());

        assertEquals(userEmail, user.getEmail());
    }

    @Test
    void testGetUserEmailWhenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> this.serviceToTest.getUserEmail(100L));
    }

    @Test
    void testDeleteUser() {
        List<User> users = this.userRepository.findAll();

        assertEquals(2, users.size());

        this.serviceToTest.deleteUser(user.getId());

         users = this.userRepository.findAll();

        assertEquals(1, users.size());
        assertEquals(admin.getEmail(), this.userRepository.findAll().get(0).getEmail());
    }

    @Test
    void testDeleteUserWhenAdmin() {
        assertThrows(NotAllowedException.class, () -> this.serviceToTest.deleteUser(admin.getId()));
    }






}