package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CheckoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CheckoutController {

    private BookService bookService;
    private CheckoutService checkoutService;

    public CheckoutController(BookService bookService, CheckoutService checkoutService) {
        this.bookService = bookService;
        this.checkoutService = checkoutService;
    }


    @GetMapping("/checkout/book/{id}")
    public String checkoutBook(@PathVariable Long id, Model model) {
        BookDTO book = this.checkoutService.getBook(id);
        model.addAttribute("book", book);

        return "checkout";
    }
}
