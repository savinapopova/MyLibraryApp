package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.book.AddBookDTO;
import com.example.mylibrary.model.dto.message.MessageDTO;
import com.example.mylibrary.model.dto.message.MessageResponseDTO;
import com.example.mylibrary.model.dto.user.UserDTO;

import java.util.List;

public interface AdminService {
    List<MessageDTO> getOpenMessages();

    void sendResponse(Long messageId, MessageResponseDTO messageResponseDTO, String email);

    void postBook(AddBookDTO addBookDTO);

    void increaseBookQuantity(Long id);

    void decreaseBookQuantity(Long id);

    void deleteBook(Long id);

    List<UserDTO> getAllUsersExceptPrincipal(String email);

    void addAdmin(Long id);

    void removeAdmin(Long id, String email);

    String getUserEmail(Long id);

    void deleteUser(Long id);
}
