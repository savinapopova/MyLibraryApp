package com.example.mylibrary.service;

import com.example.mylibrary.model.dto.HistoryDTO;
import com.example.mylibrary.model.entity.History;
import com.example.mylibrary.repository.HistoryRepository;
import org.modelmapper.ModelMapper;
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
    public List<HistoryDTO> getUserHistories(Principal principal) {

        List<History> histories = this.historyRepository.findAllByUserEmail(principal.getName());

       return histories.stream()
                .map(h -> modelMapper.map(h, HistoryDTO.class))
                .collect(Collectors.toList());
    }
}
