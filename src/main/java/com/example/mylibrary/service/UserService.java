package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.user.UserRegisterDTO;
import com.example.mylibrary.model.entity.User;

import java.util.List;

public interface UserService {
    void register(UserRegisterDTO userRegisterDTO);

    void saveUser(User user);

    List<User> findAllUsers();

    User getUser(Long id);

    void deleteUser(Long id);

    User getUser(String email);
}
