package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.UserRegisterDTO;
import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.RoleRepository;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.RoleService;
import com.example.mylibrary.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;



    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        User user = new User(userRegisterDTO.getFirstName(),
                userRegisterDTO.getLastName(),
                userRegisterDTO.getEmail(),
                passwordEncoder.encode(userRegisterDTO.getPassword()));
        Role userRole = this.roleService.findByName(RoleName.USER);
                user.getRoles().add(userRole);
        this.userRepository.save(user);
    }


    @Override
    public User getLoggedUser(Principal principal) {
        // TODO: handle exception
        String email = principal.getName();
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException();
        }
        return optionalUser.get();
    }

    @Override
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {

        return this.userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        // TODO: handle exception
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException();
        }
        return optionalUser.get();
    }


}
