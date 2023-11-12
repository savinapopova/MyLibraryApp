package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.PostMessageDTO;

import java.security.Principal;
import java.util.List;

public interface MessageService {
    void registerMessage(PostMessageDTO postMessageDTO, Principal principal);

    List<MessageDTO> getUsersMessages(Principal principal);
}
