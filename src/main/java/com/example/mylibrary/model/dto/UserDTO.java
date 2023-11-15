package com.example.mylibrary.model.dto;

import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.model.enums.RoleName;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Long id;

    private String email;

    private String roles;

    private boolean isActive;

    private boolean isAdmin;

    public UserDTO() {
    }
}
