package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.PostMessageDTO;
import com.example.mylibrary.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ModelAttribute
    public PostMessageDTO init() {
        return new PostMessageDTO();
    }

    @GetMapping("/messages")
    public String getMessages(Model model) {
        return "messages";
    }

    @PostMapping("/messages/send")
    public String postMessage(@Valid PostMessageDTO postMessageDTO, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes, Principal principal) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("postMessageDTO", postMessageDTO);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.postMessageDTO",
                            bindingResult);
            return "redirect:/messages";
        }
        this.messageService.registerMessage(postMessageDTO, principal);

        redirectAttributes.addFlashAttribute("postMessageDTO", postMessageDTO);
        redirectAttributes.addFlashAttribute("successMessage", true);


        return "redirect:/messages";
    }
}
