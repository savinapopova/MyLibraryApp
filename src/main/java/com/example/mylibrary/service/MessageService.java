package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.PostMessageDTO;

import java.security.Principal;

public interface MessageService {
    void registerMessage(PostMessageDTO postMessageDTO, Principal principal);
}
