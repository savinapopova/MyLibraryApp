package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.RoleRepository;
import com.example.mylibrary.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void seedRoles() {
        if (this.roleRepository.count() == 0) {
            List<Role> roles = Arrays.stream(RoleName.values())
                    .map(Role::new)
                    .collect(Collectors.toList());
            this.roleRepository.saveAll(roles);
        }
    }
}
