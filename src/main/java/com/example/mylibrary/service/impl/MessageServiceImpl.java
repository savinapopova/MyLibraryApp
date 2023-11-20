package com.example.mylibrary.service.impl;

import com.example.mylibrary.errors.ObjectNotFoundException;
import com.example.mylibrary.model.dto.MessageDTO;
import com.example.mylibrary.model.dto.PostMessageDTO;
import com.example.mylibrary.model.entity.Message;
import com.example.mylibrary.model.entity.User;
import com.example.mylibrary.repository.MessageRepository;
import com.example.mylibrary.service.MessageService;
import com.example.mylibrary.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    private UserService userService;

    private ModelMapper modelMapper;

    public MessageServiceImpl(MessageRepository messageRepository, UserService userService, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void registerMessage(PostMessageDTO postMessageDTO, String userEmail) {

        Message message = modelMapper.map(postMessageDTO, Message.class);
        User user = this.userService.getUser(userEmail);
        message.setUser(user);
        this.messageRepository.save(message);

    }

    @Override
    public List<MessageDTO> getUsersMessages(String userEmail) {
      List<Message> messages = this.messageRepository.findAllByUserEmail(userEmail);

      return messages.stream().map(message -> this.modelMapper.map(message, MessageDTO.class))
              .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getOpenMessages() {
        return this.messageRepository.findAllByClosed(false)
                .stream().map(m -> modelMapper.map(m, MessageDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Message getMessage(Long id) {
        // TODO: handled
        Optional<Message> optionalMessage = this.messageRepository.findById(id);

        if (optionalMessage.isEmpty()) {
            throw new ObjectNotFoundException("message not found");
        }
        return optionalMessage.get();
    }

    @Override
    public void answerMessage(Long id, String response, User admin) {
        Message message = getMessage(id);
        message.setResponse(response);
        message.setAdmin(admin);
        message.setClosed(true);
        this.messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long id) {

        Message message = getMessage(id);

        this.messageRepository.delete(message);
    }

    @Override
    public void deleteUserMessages(Long userId) {
        this.messageRepository.deleteAllByUserId(userId);
    }
}
