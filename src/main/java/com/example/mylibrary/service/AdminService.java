package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;

import java.security.Principal;
import java.util.List;

public interface AdminService {
    List<MessageDTO> getOpenMessages();

    void sendResponse(Long messageId, MessageResponseDTO messageResponseDTO, Principal principal);
}
