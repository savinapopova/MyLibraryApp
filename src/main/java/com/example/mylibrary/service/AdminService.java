package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.AddBookDTO;
import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;
import com.example.mylibrary.model.dto.UserDTO;

import java.security.Principal;
import java.util.List;

public interface AdminService {
    List<MessageDTO> getOpenMessages();

    void sendResponse(Long messageId, MessageResponseDTO messageResponseDTO, Principal principal);

    void postBook(AddBookDTO addBookDTO);

    void increaseBookQuantity(Long id);

    void decreaseBookQuantity(Long id);

    void deleteBook(Long id);

    List<UserDTO> getAllUsersExceptPrincipal(Principal principal);

    void addAdmin(Long id);

    void removeAdmin(Long id, Principal principal);

    String getUserEmail(Long id);

    void deleteUser(Long id);
}
