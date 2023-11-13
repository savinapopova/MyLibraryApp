package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.MessageResponseDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.service.AdminService;
import com.example.mylibrary.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private MessageRepository messageRepository;

    private UserService userService;

    private ModelMapper modelMapper;

    public AdminServiceImpl(MessageRepository messageRepository, UserService userService, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<MessageDTO> getOpenMessages() {
        return this.messageRepository.findAllByClosed(false)
                .stream().map(m -> modelMapper.map(m, MessageDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void sendResponse(Long messageId, MessageResponseDTO messageResponseDTO,
                             Principal principal) {
        // TODO: handle exception
        Optional<Message> optionalMessage = this.messageRepository.findById(messageId);

        if (optionalMessage.isEmpty()) {
            throw new NoSuchElementException();
        }

        User admin = userService.getLoggedUser(principal);
        Message message = optionalMessage.get();
        message.setResponse(messageResponseDTO.getResponse());
        message.setAdmin(admin);
        message.setClosed(true);
        this.messageRepository.save(message);

    }


}
