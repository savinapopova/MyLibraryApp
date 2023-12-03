package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.message.MessageDTO;
import com.example.mylibrary.model.dto.message.PostMessageDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;

import java.util.List;

public interface MessageService {
    void registerMessage(PostMessageDTO postMessageDTO, String userEmail);

    List<MessageDTO> getUsersMessages(String userEmail);

    List<MessageDTO> getOpenMessages();

    Message getMessage(Long id);

    void answerMessage(Long messageId, String response, User admin);

    void deleteMessage(Long id, String userEmail);

    void deleteUserMessages(Long userId);
}
