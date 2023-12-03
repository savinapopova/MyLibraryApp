package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.message.MessageDTO;
import com.example.mylibrary.model.dto.message.MessageResponseDTO;
import com.example.mylibrary.model.dto.user.UserDTO;
import com.example.mylibrary.model.entity.*;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.service.RoleService;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ModelMapper modelMapper;

    private User user;

    private User admin;

    private Message openMessage1;

    private Message openMessage2;

    private Message openMessage3;

    private Message closedMessage;

    private Book book1;

    private Book book2;

    private Book book3;

    private Checkout checkout1;

    private Checkout checkout2;


    @BeforeEach
    public void setUp() {
        this.messageRepository.deleteAll();
        this.checkoutRepository.deleteAll();
        this.bookRepository.deleteAll();
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

        Category biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Category cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);

        book1 = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        book2 = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 0, cookbook);
        book3 = new Book(3L, "title3", "author3",
                "image3", "description3", 0, 0, cookbook);

        checkout1 = new Checkout(book1, user);
        checkout2 = new Checkout(book2, user);
    }

    @AfterEach
    public void tearDown() {
        this.messageRepository.deleteAll();
        this.checkoutRepository.deleteAll();
        this.bookRepository.deleteAll();
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

    @Test
    void testPostResponseWhenAnonymous() throws Exception {
        messageRepository.save(openMessage1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/message/reply/{id}", openMessage1.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
        ;

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testPostResponseWhenNotAdmin() throws Exception {
        messageRepository.save(openMessage1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/message/reply/{id}", openMessage1.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden());
        ;

    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testPostResponseWhenAdmin() throws Exception {

        this.messageRepository.save(openMessage1);
        this.messageRepository.save(openMessage2);
        this.messageRepository.save(closedMessage);

        MessageResponseDTO messageResponseDTO = new MessageResponseDTO();
        messageResponseDTO.setResponse("response");

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/message/reply/{id}", openMessage1.getId())
                        .with(csrf())
                .param("response", "response"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"));

        Message message = this.messageRepository.findById(openMessage1.getId()).orElse(null);

        assertEquals("response", message.getResponse());
        assertEquals(admin, message.getAdmin());
        assertTrue(message.isClosed());


    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testPostResponseWithInvalidData() throws Exception {
        messageRepository.save(openMessage1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/message/reply/{id}", openMessage1.getId())
                        .with(csrf())
                        .param("response", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/messages"))
                .andExpect(flash().attributeExists("messageResponseDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.messageResponseDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.messageResponseDTO",
                        hasProperty("fieldErrors", hasSize(1))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.messageResponseDTO",
                        hasProperty("fieldErrors", hasItem(
                                hasProperty("field", is("response"))
                        ))));


        Message message = this.messageRepository.findById(openMessage1.getId()).orElse(null);

        assertNull(message.getResponse());
        assertNull(message.getAdmin());
        assertFalse(message.isClosed());

    }

    @Test
    void testAddBookWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/book/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/book/add"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookWhenAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/book/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("addBookDTO"))
                .andExpect(view().name("book-add"))
                .andExpect(forwardedUrl(null));
    }

    @Test
    void testAddBookPostWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWhenAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf())
                        .param("title", "title")
                        .param("author", "author")
                        .param("image", "image")
                        .param("description", "description")
                        .param("copies", "1")
                        .param("category", "BIOGRAPHY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book/add"))
                .andExpect(flash().attributeExists("sentSuccess"))
                .andExpect(flash().attribute("sentSuccess", true))
                .andExpect(model().hasNoErrors());
        ;
        List<Book> books = this.bookRepository.findAll();
        assertEquals(1, books.size());
        assertEquals("title", books.get(0).getTitle());
        assertEquals("author", books.get(0).getAuthor());
        assertEquals("image", books.get(0).getImage());
        assertEquals("description", books.get(0).getDescription());
        assertEquals(1, books.get(0).getCopies());
        assertEquals(1, books.get(0).getCopiesAvailable());
        assertEquals("BIOGRAPHY", books.get(0).getCategory().getName().name());

    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWithNoTitle() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf())
                        .param("title", "")
                        .param("author", "author")
                        .param("image", "image")
                        .param("description", "description")
                        .param("copies", "1")
                        .param("category", "BIOGRAPHY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book/add"))
                .andExpect(flash().attributeExists("addBookDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.addBookDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasSize(1))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasItem(
                                hasProperty("field", is("title"))
                        ))));
        ;
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWithNoAuthor() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf())
                        .param("title", "title")
                        .param("author", "")
                        .param("image", "image")
                        .param("description", "description")
                        .param("copies", "1")
                        .param("category", "BIOGRAPHY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book/add"))
                .andExpect(flash().attributeExists("addBookDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.addBookDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasSize(1))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasItem(
                                hasProperty("field", is("author"))
                        ))));
        ;
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWithNoImage() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf())
                        .param("title", "title")
                        .param("author", "author")
                        .param("image", "")
                        .param("description", "description")
                        .param("copies", "1")
                        .param("category", "BIOGRAPHY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book/add"))
                .andExpect(flash().attributeExists("addBookDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.addBookDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasSize(1))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasItem(
                                hasProperty("field", is("image"))
                        ))));
        ;
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWithNoDescription() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf())
                        .param("title", "title")
                        .param("author", "author")
                        .param("image", "image")
                        .param("description", "")
                        .param("copies", "1")
                        .param("category", "BIOGRAPHY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book/add"))
                .andExpect(flash().attributeExists("addBookDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.addBookDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasSize(1))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasItem(
                                hasProperty("field", is("description"))
                        ))));
        ;
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWithNegativeQuantity() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf())
                        .param("title", "title")
                        .param("author", "author")
                        .param("image", "image")
                        .param("description", "description")
                        .param("copies", "-5")
                        .param("category", "BIOGRAPHY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book/add"))
                .andExpect(flash().attributeExists("addBookDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.addBookDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasSize(1))))
                        .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                                hasProperty("fieldErrors", hasItem(
                                        hasProperty("field", is("copies"))))));
        ;
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddBookPostWithNoCategory() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/admin/book/add")
                        .with(csrf())
                        .param("title", "title")
                        .param("author", "author")
                        .param("image", "image")
                        .param("description", "description")
                        .param("copies", "5")
                        .param("category", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/book/add"))
                .andExpect(flash().attributeExists("addBookDTO"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.addBookDTO"))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasSize(1))))
                .andExpect(flash().attribute("org.springframework.validation.BindingResult.addBookDTO",
                        hasProperty("fieldErrors", hasItem(
                                hasProperty("field", is("category"))
                        ))));
        ;
    }

    @Test
    void testChangeQuantityWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/quantity"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testChangeQuantityWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/quantity"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testChangeQuantityWhenAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/quantity"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("books"))
                .andExpect(view().name("change-quantity"))
                .andExpect(forwardedUrl(null));
    }

    @Test
    void testIncreaseQuantityWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/increase/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testIncreaseQuantityWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/increase/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testIncreaseQuantity() throws Exception {
        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/increase/{id}", book1.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quantity"));

        Book book = this.bookRepository.findById(book1.getId()).orElse(null);
        assertEquals(2, book.getCopies());
        assertEquals(2, book.getCopiesAvailable());
        ;
    }

    @Test
    void testDecreaseQuantityWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/decrease/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testDecreaseQuantityWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/decrease/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDecreaseQuantity() throws Exception {
        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/decrease/{id}", book1.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quantity"));

        Book book = this.bookRepository.findById(book1.getId()).orElse(null);
        assertEquals(0, book.getCopies());
        assertEquals(0, book.getCopiesAvailable());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDecreaseQuantityWhenNoCopiesAvailable() throws Exception {
        this.bookRepository.save(book2);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/decrease/{id}", book2.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quantity"));

        Book book = this.bookRepository.findById(book2.getId()).orElse(null);
        assertEquals(2, book.getCopies());
        assertEquals(0, book.getCopiesAvailable());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDecreaseQuantityWhenNoCopies() throws Exception {
        this.bookRepository.save(book3);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/quantity/decrease/{id}", book3.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quantity"));

        Book book = this.bookRepository.findById(book3.getId()).orElse(null);
        assertEquals(0, book.getCopies());
        assertEquals(0, book.getCopiesAvailable());
    }

    @Test
    void testDeleteBookWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/book/delete/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testDeleteBookWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/book/delete/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteBook() throws Exception {
        this.bookRepository.save(book1);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/book/delete/{id}", book1.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/quantity"));

        List<Book> books = this.bookRepository.findAll();
        assertEquals(0, books.size());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteBookWhenNotExists() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/book/delete/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("object-not-found"))
            .andExpect(forwardedUrl(null))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "book with id 1 not found"));

        List<Book> books = this.bookRepository.findAll();
        assertEquals(0, books.size());
    }

    @Test
    void testAddAdminRoleWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/users/add-admin/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testAddAdminRoleWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/users/add-admin/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddAdminRole() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/manage/role/add/{id}", user.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        User user = this.userRepository.findById(this.user.getId()).orElse(null);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);

        assertTrue(user.getRoles().contains(adminRole));
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddAdminRoleWhenAlreadyAdmin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/manage/role/add/{id}", admin.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        User admin = this.userRepository.findById(this.admin.getId()).orElse(null);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);

        assertTrue(admin.getRoles().contains(adminRole));
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testAddAdminRoleWhenUserNotExists() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/manage/role/add/{id}",10L)
                        .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("object-not-found"))
                .andExpect(forwardedUrl(null))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "user with id 10 not found"));

        User admin = this.userRepository.findById(this.admin.getId()).orElse(null);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);

        assertTrue(admin.getRoles().contains(adminRole));
    }

    @Test
    void testRemoveAdminRoleWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/users/remove-admin/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testRemoveAdminRoleWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/users/remove-admin/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testRemoveAdminRole() throws Exception {
        User secondAdmin = testUserData.createSecondAdmin();

        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/manage/role/remove/{id}", secondAdmin.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));


        User admin2 = this.userRepository.findById(secondAdmin.getId()).orElse(null);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);
        assertFalse(admin2.getRoles().contains(adminRole));
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testRemoveAdminRoleWhenSameAdmin() throws Exception {


        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/manage/role/remove/{id}", admin.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));


    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testRemoveAdminRoleWhenAdminNotExists() throws Exception {


        this.mockMvc.perform(MockMvcRequestBuilders.put("/admin/manage/role/remove/{id}", 10L)
                        .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("object-not-found"))
                .andExpect(forwardedUrl(null))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "user with id 10 not found"));

    }

    @Test
    void testDeleteUserWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/delete/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testDeleteUserWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/delete/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteUser() throws Exception {

        User secondAdmin = testUserData.createSecondAdmin();



        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/delete/{id}", user.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        List<User> users = this.userRepository.findAll();
        assertEquals(2, users.size());
        assertEquals("adminFirstName", users.get(0).getFirstName());
        assertEquals("adminLastName", users.get(0).getLastName());
        assertEquals("adminEmail", users.get(0).getEmail());
        assertEquals("admin2FirstName", users.get(1).getFirstName());
        assertEquals("admin2LastName", users.get(1).getLastName());
        assertEquals("admin2Email", users.get(1).getEmail());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteUserWhenUserIsAdmin() throws Exception {

        User secondAdmin = testUserData.createSecondAdmin();



        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/delete/{id}", secondAdmin.getId())
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());


        List<User> users = this.userRepository.findAll();
        assertEquals(3, users.size());
        assertEquals("userFirstName", users.get(0).getFirstName());
        assertEquals("userLastName", users.get(0).getLastName());
        assertEquals("userEmail", users.get(0).getEmail());
        assertEquals("adminFirstName", users.get(1).getFirstName());
        assertEquals("adminLastName", users.get(1).getLastName());
        assertEquals("adminEmail", users.get(1).getEmail());
        assertEquals("admin2FirstName", users.get(2).getFirstName());
        assertEquals("admin2LastName", users.get(2).getLastName());
        assertEquals("admin2Email", users.get(2).getEmail());

        ;
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteUserWhenNotExists() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/delete/{id}", 10L)
                        .with(csrf()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("object-not-found"))
                .andExpect(forwardedUrl(null))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "user with id 10 not found"));


    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testDeleteUserWhenHasCheckouts() throws Exception {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);

        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/delete/{id}", user.getId())
                        .with(csrf()))
                .andExpect(status().isMethodNotAllowed());

        List<User> users = this.userRepository.findAll();
        assertEquals(2, users.size());
        assertEquals("userFirstName", users.get(0).getFirstName());
        assertEquals("userLastName", users.get(0).getLastName());
        assertEquals("userEmail", users.get(0).getEmail());
        assertEquals("adminFirstName", users.get(1).getFirstName());
        assertEquals("adminLastName", users.get(1).getLastName());
        assertEquals("adminEmail", users.get(1).getEmail());

    }


        @Test
    void testUserCheckoutsWhenAnonymous() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/checkouts/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        void testUserCheckoutsWhenNotAdmin() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/checkouts/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testUserCheckoutsWhenUserNotExists() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/checkouts/{id}", 10L))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("object-not-found"))
                .andExpect(forwardedUrl(null))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "user with id 10 not found"));
    }

    @Test
    @WithUserDetails(value = "adminEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testUserCheckouts() throws Exception {

        this.bookRepository.save(book1);
        this.bookRepository.save(book2);
        this.checkoutRepository.save(checkout1);
        this.checkoutRepository.save(checkout2);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/checkouts/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("checkouts"))
                .andExpect(model().attribute("checkouts", hasSize(2)))
                .andExpect(view().name("checkouts-admin"))
                .andExpect(forwardedUrl(null));
    }




}