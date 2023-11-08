package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.BookDTO;

public interface CheckoutService {
    BookDTO getBook(Long id);
}
