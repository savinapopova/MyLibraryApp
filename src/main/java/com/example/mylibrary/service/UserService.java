package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.UserRegisterDTO;
import com.example.mylibrary.model.entity.User;

import java.security.Principal;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);

    int getLoansCount(Principal principal);

    boolean isAlreadyCheckedOutByUser(Long id, Principal principal);

    User getLoggedUser(Principal principal);

    void saveUser(User user);
}
