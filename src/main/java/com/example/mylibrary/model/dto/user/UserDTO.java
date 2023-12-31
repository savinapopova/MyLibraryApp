package com.example.mylibrary.model.dto.user;

import com.example.mylibrary.model.enums.CategoryName;
import com.example.mylibrary.model.enums.RoleName;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private String roles;

    private boolean isActive;

    private boolean isAdmin;

    public UserDTO() {
    }
}
