package com.example.mylibrary.service.impl;

import com.example.mylibrary.event.BookReturnedEvent;
import com.example.mylibrary.model.dto.history.HistoryDTO;
import com.example.mylibrary.model.entity.*;
import com.example.mylibrary.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryServiceImplTest {

    @Mock
    private HistoryRepository mockHistoryRepository;


    @Mock
    private ModelMapper mockModelMapper;

    private HistoryServiceImpl serviceToTest;

    private History history;

    private Checkout checkout;

    private User user;

    private Book book;

    @BeforeEach
    void setUp() {
        serviceToTest = new HistoryServiceImpl(mockHistoryRepository, mockModelMapper);
        book = new Book();
        user = new User();
        checkout = new Checkout(book, user);
        history = new History(checkout);


    }

    @Test
    void testGetUserHistories() {
        String validEmail = "validEmail";
        String invalidEmail = "invalidEmail";
        History history1 = new History();
        History history2 = new History();
        History history3 = new History();
        when(mockHistoryRepository.findAllByUserEmailOrderByReturnDateDesc(validEmail)).thenReturn(List.of(history1, history2, history3));
        when(mockHistoryRepository.findAllByUserEmailOrderByReturnDateDesc(invalidEmail)).thenReturn(List.of());

        when(mockModelMapper.map(history1, HistoryDTO.class)).thenReturn(new HistoryDTO());
        when(mockModelMapper.map(history2, HistoryDTO.class)).thenReturn(new HistoryDTO());
        when(mockModelMapper.map(history3, HistoryDTO.class)).thenReturn(new HistoryDTO());


        List<HistoryDTO> result = serviceToTest.getUserHistories(validEmail);
        assertEquals(3, result.size());
        assertEquals(HistoryDTO.class, result.get(0).getClass());

        List<HistoryDTO> emptyResult = serviceToTest.getUserHistories(invalidEmail);
        assertEquals(0, emptyResult.size());


    }

    @Test
    void testRegisterHistory() {

        BookReturnedEvent bookReturnedEvent = new BookReturnedEvent(this).setCheckout(checkout);

       serviceToTest.registerHistory(bookReturnedEvent);
        verify(mockHistoryRepository).save(history);
    }

    @Test
    void testDeleteBookHistories() {
        Long validId = 1L;

        serviceToTest.deleteBookHistories(validId);
        verify(mockHistoryRepository).deleteAllByBookId(validId);

    }

    @Test
    void testDeleteUserHistories() {
        Long validId = 1L;

        serviceToTest.deleteUserHistories(validId);
        verify(mockHistoryRepository).deleteAllByUserId(validId);
    }



}