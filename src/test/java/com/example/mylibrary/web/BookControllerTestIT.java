package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.SearchBookDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Category;
import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.service.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookControllerTestIT {

    @Autowired
    private BookController controllerToTest;

    @Autowired
    private BookService bookService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapper modelMapper;

    private User user;

    private User admin;
    @Autowired
    private CategoryService categoryService;

    private Book biographyBook;
    private Book cookBook;
    private Book fantasyBook;
    private Book educationBook;
    private Book childrenBook;


    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();

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

        Category biography = this.categoryService.getCategory(CategoryName.BIOGRAPHY);
        Category cookbook = this.categoryService.getCategory(CategoryName.COOKBOOK);
        Category fantasy = this.categoryService.getCategory(CategoryName.FANTASY);
        Category education = this.categoryService.getCategory(CategoryName.EDUCATION);
        Category children = this.categoryService.getCategory(CategoryName.CHILDREN);

        biographyBook = new Book(1L, "title1", "author1",
                "image1", "description1", 1, 1, biography);
        cookBook = new Book(2L, "title2", "author2",
                "image2", "description2", 2, 2, cookbook);
        fantasyBook = new Book(3L, "title3", "author3",
                "image3", "description3", 0, 0, fantasy);
        educationBook = new Book(4L, "title4", "author4",
                "image4", "description4", 2, 0, education);
        childrenBook = new Book(5L, "title5", "author5",
                "image5", "description5", 2, 1, children);


    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testSearchWithAnonymousUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithMockUser(username = "userEmail", roles = {"USER", "ADMIN"})
    void testSearchWithAuthenticatedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("search/all"));
    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testSearchByCategoryWithAuthenticatedUser() throws Exception {


        bookRepository.save(biographyBook);

        mockMvc.perform(MockMvcRequestBuilders.get("/search/{category}", "BIOGRAPHY"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of(modelMapper.map(biographyBook, SearchBookDTO.class))))
                .andExpect(model().attribute("books", hasSize(1)));

    }


    @Test
    void testSearchByCategoryWithAnonymousUser() throws Exception {
        bookRepository.save(biographyBook);

        mockMvc.perform(MockMvcRequestBuilders.get("/search/{category}", "BIOGRAPHY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetBookByIdWithAuthenticatedUser() throws Exception {

        bookRepository.save(biographyBook);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"));

    }

    @Test
    void testGetBookByIdWithAnonymousUser() throws Exception {

        bookRepository.save(biographyBook);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));

    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testSearchByTitleWithAuthenticatedUserWithoutCategory() throws Exception {

        bookRepository.save(biographyBook);
        bookRepository.save(cookBook);
        bookRepository.save(fantasyBook);
        bookRepository.save(educationBook);
        bookRepository.save(childrenBook);

        mockMvc.perform(MockMvcRequestBuilders.post("/search/{category}", "All")
                .param("title", "title")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of(modelMapper.map(List.of(biographyBook, cookBook, fantasyBook, educationBook, childrenBook), SearchBookDTO[].class))))
                .andExpect(model().attribute("books", hasSize(5)));

        mockMvc.perform(MockMvcRequestBuilders.post("/search/{category}", "All")
                        .param("title", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of(modelMapper.map(biographyBook, SearchBookDTO.class))))
                .andExpect(model().attribute("books", hasSize(1)));

    }

    @Test
    void testSearchByTitleWithAnonymousUserWithoutCategory() throws Exception {



        mockMvc.perform(MockMvcRequestBuilders.post("/search/{category}", "All")
                        .param("title", "title")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));


    }

    @Test
    @WithUserDetails(value = "userEmail", userDetailsServiceBeanName = "userDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testSearchByTitleWithAuthenticatedUserWithCategory() throws Exception {

        bookRepository.save(biographyBook);
        bookRepository.save(cookBook);
        bookRepository.save(fantasyBook);
        bookRepository.save(educationBook);
        bookRepository.save(childrenBook);

        mockMvc.perform(MockMvcRequestBuilders.post("/search/{category}", "BIOGRAPHY")
                        .param("title", "title")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of(modelMapper.map(biographyBook, SearchBookDTO.class))))
                .andExpect(model().attribute("books", hasSize(1)));

        mockMvc.perform(MockMvcRequestBuilders.post("/search/{category}", "BIOGRAPHY")
                        .param("title", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", List.of()))
                .andExpect(model().attribute("books", hasSize(0)));



    }



}