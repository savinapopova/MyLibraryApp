package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.book.BookDTO;
import com.example.mylibrary.model.dto.checkout.CheckOutDTO;
import com.example.mylibrary.model.dto.history.HistoryDTO;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.service.BookService;
import com.example.mylibrary.service.CheckoutService;
import com.example.mylibrary.service.HistoryService;
import com.example.mylibrary.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

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


        int booksCount =  this.checkoutService.getLoansCount(principal.getName());

        model.addAttribute("booksCount", booksCount);


        boolean alreadyCheckedOut = this.checkoutService.bookAlreadyCheckedOutByUser(id, principal.getName());


        model.addAttribute("alreadyCheckedOut", alreadyCheckedOut);

       boolean userBlocked = this.checkoutService.isUserBlocked(principal.getName());
       model.addAttribute("userBlocked", userBlocked);

        return "checkout";
    }

    @PostMapping("/checkout/book/{id}")
    public String checkoutBook(@PathVariable Long id,Principal principal) {

        this.checkoutService.checkoutBook(id, principal.getName());

        return "redirect:/checkout/book/" + id;
    }

    @GetMapping("/shelf")
    public String shelf(Model model, Principal principal) {
        User loggedUser = this.userService.getUser(principal.getName());
        List<CheckOutDTO> loans = this.checkoutService.getUserCheckouts(loggedUser.getId());
       model.addAttribute("loans", loans);
       List<HistoryDTO> histories = this.historyService.getUserHistories(principal.getName());
         model.addAttribute("histories", histories);
       return "shelf";
    }

    @PutMapping("/return/book/{id}")
    public String returnBook(@PathVariable Long id, Principal principal) {
        this.checkoutService.returnBook(id, principal.getName());
        return "redirect:/shelf";
    }

    @PutMapping("/renew/book/{id}")
    public String renewCheckout(@PathVariable Long id, Principal principal) {
        this.checkoutService.renewCheckout(id, principal.getName());
        return "redirect:/shelf";
    }
}
