package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.PostMessageDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;

import java.security.Principal;
import java.util.List;

public interface MessageService {
    void registerMessage(PostMessageDTO postMessageDTO, Principal principal);

    List<MessageDTO> getUsersMessages(Principal principal);

    List<MessageDTO> getOpenMessages();

    Message getMessage(Long id);

    void answerMessage(Long messageId, String response, User admin);

    void deleteMessage(Long id);

    void deleteUserMessages(Long userId);
}
