package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.dto.CheckOutDTO;
import com.example.mylibrary.model.dto.HistoryDTO;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CheckoutService;
import com.example.mylibrary.service.HistoryService;
import com.example.mylibrary.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class CheckoutController {

    private BookService bookService;
    private CheckoutService checkoutService;

    private HistoryService historyService;

    private UserService userService;

    public CheckoutController(BookService bookService, CheckoutService checkoutService,
                              HistoryService historyService, UserService userService) {
        this.bookService = bookService;
        this.checkoutService = checkoutService;
        this.historyService = historyService;
        this.userService = userService;
    }


    @GetMapping("/checkout/book/{id}")
    public String checkoutBook(@PathVariable Long id, Model model,Principal principal) {
        BookDTO book = this.bookService.getBookDTO(id);
        model.addAttribute("book", book);

        int booksCount =  this.userService.getLoansCount(principal);

        model.addAttribute("booksCount", booksCount);

        boolean alreadyCheckedOut = this.userService.isAlreadyCheckedOutByUser(id, principal);

        model.addAttribute("alreadyCheckedOut", alreadyCheckedOut);

        return "checkout";
    }

    @PostMapping("/checkout/book/{id}")
    public String checkoutBook(@PathVariable Long id,Principal principal) {

        this.checkoutService.checkoutBook(id, principal);

        return "redirect:/checkout/book/" + id;
    }

    @GetMapping("/shelf")
    public String shelf(Model model, Principal principal) {
       List<CheckOutDTO> loans = this.checkoutService.getUserCheckouts(principal);
       model.addAttribute("loans", loans);
       List<HistoryDTO> histories = this.historyService.getUserHistories(principal);
         model.addAttribute("histories", histories);
       return "shelf";
    }
}
