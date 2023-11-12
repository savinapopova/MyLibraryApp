package com.example.mylibrary.service.impl;

import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
