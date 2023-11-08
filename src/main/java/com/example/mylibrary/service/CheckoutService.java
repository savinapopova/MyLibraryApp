package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.BookDTO;

import java.security.Principal;

public interface CheckoutService {
    BookDTO getBook(Long id);

    void checkoutBook(Long id, Principal principal);
}
