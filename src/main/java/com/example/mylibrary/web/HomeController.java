package com.example.mylibrary.web;

import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    private UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String home(Model model, Principal principal) {

        if (principal != null) {
            User loggedUser = userService.getLoggedUser(principal);
            model.addAttribute("name", loggedUser.getFirstName());
        }

        return "index";
    }
}
