package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.*;
import com.example.mylibrary.service.AdminService;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CheckoutService;
import com.example.mylibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;

    private BookService bookService;

    private CheckoutService checkoutService;




    public AdminController(AdminService adminService, BookService bookService,
                           CheckoutService checkoutService, UserService userService) {
        this.adminService = adminService;
        this.bookService = bookService;
        this.checkoutService = checkoutService;
    }

    @ModelAttribute
    public MessageResponseDTO initMessageResponseDTO() {
        return new MessageResponseDTO();
    }

    @ModelAttribute
    public AddBookDTO initAddBook() {
        return new AddBookDTO();
    }

    @GetMapping("/users")
    public String users(Model model, Principal principal) {
        List<UserDTO> allUsers = this.adminService.getAllUsersExceptPrincipal(principal);
        model.addAttribute("allUsers", allUsers);

        return "people";
    }

    @GetMapping("/messages")
    public String getOpenMessages(Model model) {

        List<MessageDTO> messages = this.adminService.getOpenMessages();
        model.addAttribute("messages", messages);

        return "messages-admin";
    }

    @PutMapping("/message/reply/{id}")
    public String postResponse(@Valid MessageResponseDTO messageResponseDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               @PathVariable Long id, Principal principal) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("messageResponseDTO", messageResponseDTO);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.messageResponseDTO",
                            bindingResult);

            return "redirect:/admin/messages";
        }

        this.adminService.sendResponse(id, messageResponseDTO, principal);

        return "redirect:/admin/messages";
    }

    @GetMapping("/book/add")
    public String addBook() {
        return "book-add";
    }

    @PostMapping("/book/add")
    public String addBook(@Valid AddBookDTO addBookDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("addBookDTO", addBookDTO);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.addBookDTO",
                            bindingResult);

            return "redirect:/admin/book/add";
        }
        this.adminService.postBook(addBookDTO);
        redirectAttributes.addFlashAttribute("sentSuccess", true);

        return "redirect:/admin/book/add";
    }

    @GetMapping("/quantity")
    public String changeQuantity(Model model) {

        List<BookDTO> books = this.bookService.getAllBooks();
        model.addAttribute("books", books);

        return "change-quantity";
    }

    @PutMapping("/quantity/increase/{id}")
    public String increaseQuantity(@PathVariable Long id) {
        this.adminService.increaseBookQuantity(id);
        return "redirect:/admin/quantity";
    }

    @PutMapping("/quantity/decrease/{id}")
    public String decreaseQuantity(@PathVariable Long id) {
        this.adminService.decreaseBookQuantity(id);
        return "redirect:/admin/quantity";
    }

    @DeleteMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        this.adminService.deleteBook(id);
        return "redirect:/admin/quantity";
    }

    @PutMapping("/manage/role/add/{id}")
    public String addAdminRole(@PathVariable Long id) {
        this.adminService.addAdmin(id);

        return "redirect:/admin/users";
    }

    @PutMapping("/manage/role/remove/{id}")
    public String removeAdminRole(@PathVariable Long id, Principal principal) {
        this.adminService.removeAdmin(id,principal);

        return "redirect:/admin/users";
    }

    @GetMapping("/user/checkouts/{id}")
    public String userCheckouts(@PathVariable Long id, Model model){
        List<CheckOutDTO> checkouts = this.checkoutService.getUserCheckouts(id);
        model.addAttribute("checkouts", checkouts);
       String userEmail = this.adminService.getUserEmail(id);
        model.addAttribute("userEmail", userEmail);
        return "checkouts-admin";
    }

}
