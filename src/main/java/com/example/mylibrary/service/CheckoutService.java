package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.dto.CheckOutDTO;

import java.security.Principal;
import java.util.List;

public interface CheckoutService {


    void checkoutBook(Long id, Principal principal);

    List<CheckOutDTO> getUserCheckouts(Principal principal);
}
