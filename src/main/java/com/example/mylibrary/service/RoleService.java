package com.example.mylibrary.service;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.enums.RoleName;

public interface RoleService {
    void seedRoles();

    Role findByName(RoleName name);
}
