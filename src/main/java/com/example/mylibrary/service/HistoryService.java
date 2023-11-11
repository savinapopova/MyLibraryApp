package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.HistoryDTO;

import java.security.Principal;
import java.util.List;

public interface HistoryService {
    List<HistoryDTO> getUserHistories(Principal principal);
}
