package com.example.mylibrary.web;

import com.example.mylibrary.service.MessageService;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
}
