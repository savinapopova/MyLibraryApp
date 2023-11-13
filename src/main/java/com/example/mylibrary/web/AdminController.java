package com.example.mylibrary.web;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;
import com.example.mylibrary.service.AdminService;
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

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @ModelAttribute
    public MessageResponseDTO init() {
        return new MessageResponseDTO();
    }

    @GetMapping("/users")
    public String users() {
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
}
