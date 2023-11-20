package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.PostMessageDTO;
import com.example.mylibrary.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

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
    public String getMessages(Model model, Principal principal) {
        List<MessageDTO> messages = this.messageService.getUsersMessages(principal.getName());
        model.addAttribute("messages", messages);
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
        this.messageService.registerMessage(postMessageDTO, principal.getName());


        redirectAttributes.addFlashAttribute("successMessage", true);


        return "redirect:/messages";
    }

    @DeleteMapping("messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id) {
        this.messageService.deleteMessage(id);
        return "redirect:/messages";
    }
}
