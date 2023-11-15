package com.example.mylibrary.init;

import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.CategoryService;
import com.example.mylibrary.service.RoleService;
import com.example.mylibrary.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Seeder implements CommandLineRunner {

    private CategoryService categoryService;
    private RoleService roleService;

    private UserService userService;
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;


    public Seeder(CategoryService categoryService, RoleService roleService,
                  UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.categoryService = categoryService;
        this.roleService = roleService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        this.categoryService.seedCategories();
        this.roleService.seedRoles();

        seedAdmin();
    }

    private void seedAdmin() {
        if (this.userRepository.count() > 0) {
            return;
        }
        Role userRole = this.roleService.findByName(RoleName.USER);
        Role adminRole = this.roleService.findByName(RoleName.ADMIN);

        User admin = new User("Admin", "Adminov",
                "admin@email.com", passwordEncoder.encode("123456"));
        admin.getRoles().add(userRole);
        admin.getRoles().add(adminRole);
        this.userService.saveUser(admin);
    }


}
