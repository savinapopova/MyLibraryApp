package com.example.mylibrary.service.impl;

import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.model.dto.HistoryDTO;
import com.example.mylibrary.model.entity.Checkout;
import com.example.mylibrary.model.entity.History;
import com.example.mylibrary.repository.HistoryRepository;
import com.example.mylibrary.service.HistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryServiceImpl implements HistoryService {

    private HistoryRepository historyRepository;

    private ModelMapper modelMapper;

    public HistoryServiceImpl(HistoryRepository historyRepository, ModelMapper modelMapper) {
        this.historyRepository = historyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<HistoryDTO> getUserHistories(String email) {

        List<History> histories = this.historyRepository.findAllByUserEmailOrderByReturnDateDesc(email);

       return histories.stream()
                .map(h -> modelMapper.map(h, HistoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @EventListener(BookReturnedEvent.class)
    public void registerHistory(BookReturnedEvent bookReturnedEvent) {
        Checkout checkout = bookReturnedEvent.getCheckout();
        History history = new History(checkout);
        this.historyRepository.save(history);
    }

    @Override
    public void deleteBookHistories(Long id) {
        this.historyRepository.deleteAllByBookId(id);
    }

    @Override
    public void deleteUserHistories(Long userId) {
        this.historyRepository.deleteAllByUserId(userId);
    }
}
