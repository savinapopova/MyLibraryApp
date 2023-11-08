package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.UserRegisterDTO;
import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.RoleRepository;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;



    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        User user = new User(userRegisterDTO.getName(),
                userRegisterDTO.getEmail(),
                passwordEncoder.encode(userRegisterDTO.getPassword()));
        Role userRole = this.roleRepository.findByName(RoleName.USER);
                user.getRoles().add(userRole);
        this.userRepository.save(user);
    }

    @Override
    public int getLoansCount(Principal principal) {
       String email = principal.getName();
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return 0;
        }
        return optionalUser.get().getBooks().size();
    }
}
