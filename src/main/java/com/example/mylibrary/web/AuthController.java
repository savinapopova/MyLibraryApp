package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.UserRegisterDTO;
import com.example.mylibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public UserRegisterDTO init() {
        return new UserRegisterDTO();
    }

    @GetMapping("/users/register")
    public String register() {
        return "register";
    }

    @PostMapping("/users/register")
    public String register(@Valid UserRegisterDTO userRegisterDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userRegisterDTO", userRegisterDTO);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.userRegisterDTO",
                            bindingResult);
            return "redirect:/users/register";
        }
        this.userService.register(userRegisterDTO);

        return "redirect:/";

    }

    @GetMapping("/users/login")
    public String login() {
        return "login";
    }

    @PostMapping("/users/login-error")
    public String onFailedLogin(
             @ModelAttribute("email") String email,
                    Model model) {

        model.addAttribute("email", email);
        model.addAttribute("bad_credentials", "true");
        return "login";
    }

}



