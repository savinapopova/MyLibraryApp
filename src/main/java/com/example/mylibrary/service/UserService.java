package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.UserRegisterDTO;

import java.security.Principal;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);

    int getLoansCount(Principal principal);

    boolean isAlreadyCheckedOutByUser(Long id, Principal principal);
}
