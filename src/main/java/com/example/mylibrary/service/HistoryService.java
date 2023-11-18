package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.HistoryDTO;
import com.example.mylibrary.model.entity.Checkout;

import java.security.Principal;
import java.util.List;

public interface HistoryService {
    List<HistoryDTO> getUserHistories(String email);

    void registerHistory(Checkout checkout);

    void deleteBookHistories(Long id);

    void deleteUserHistories(Long userId);
}
