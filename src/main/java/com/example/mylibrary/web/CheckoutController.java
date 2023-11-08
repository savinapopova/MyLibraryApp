package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CheckoutService;
import com.example.mylibrary.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class CheckoutController {

    private BookService bookService;
    private CheckoutService checkoutService;

    private UserService userService;

    public CheckoutController(BookService bookService, CheckoutService checkoutService, UserService userService) {
        this.bookService = bookService;
        this.checkoutService = checkoutService;
        this.userService = userService;
    }


    @GetMapping("/checkout/book/{id}")
    public String checkoutBook(@PathVariable Long id, Model model,Principal principal) {
        BookDTO book = this.checkoutService.getBook(id);
        model.addAttribute("book", book);

        int booksCount =  this.userService.getLoansCount(principal);

        model.addAttribute("booksCount", booksCount);

        boolean alreadyCheckedOut = this.userService.isAlreadyCheckedOutByUser(id, principal);

        model.addAttribute("alreadyCheckedOut", alreadyCheckedOut);

        return "checkout";
    }
}
