package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.UserRegisterDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.model.entity.Role;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.model.enums.RoleName;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.RoleRepository;
import com.example.mylibrary.repository.UserRepository;
import com.example.mylibrary.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private BookRepository bookRepository;
    private PasswordEncoder passwordEncoder;



    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BookRepository bookRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bookRepository = bookRepository;
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
        User user = getLoggedUser(principal);
        return user.getBooks().size();
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
    public boolean isAlreadyCheckedOutByUser(Long id, Principal principal) {
        // TODO: handle exception
        User user = getLoggedUser(principal);
        Optional<Book> optionalBook = this.bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException();
        }
        Book book = optionalBook.get();
        if (user.getBooks().contains(book)) {
            return true;
        }
        return false;
    }
}
