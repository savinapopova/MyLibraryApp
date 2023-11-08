package com.example.mylibrary.init;

import com.example.mylibrary.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {

    private RoleService roleService;

    public RoleSeeder(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {

        this.roleService.seedRoles();
    }
}
