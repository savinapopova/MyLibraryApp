package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.UserRegisterDTO;
import com.example.mylibrary.model.entity.User;

import java.security.Principal;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);



    User getLoggedUser(Principal principal);

    void saveUser(User user);
}
