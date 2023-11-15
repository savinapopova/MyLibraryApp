package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.CheckOutDTO;

import java.security.Principal;
import java.util.List;

public interface CheckoutService {


    void checkoutBook(Long id, Principal principal);

    List<CheckOutDTO> getUserCheckouts(Long id);

    void returnBook(Long id, Principal principal);

    void renewCheckout(Long id, Principal principal);

    boolean bookAlreadyCheckedOutByUser(Long bookId, Principal principal);

    int getLoansCount(String email);

    boolean isUserBlocked(Principal principal);

    void deleteBookCheckouts(Long id);


}
