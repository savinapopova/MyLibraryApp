package com.example.mylibrary.service.impl;

import com.example.mylibrary.model.dto.BookDTO;
import com.example.mylibrary.model.entity.Book;
import com.example.mylibrary.repository.BookRepository;
import com.example.mylibrary.repository.CheckoutRepository;
import com.example.mylibrary.service.CheckoutService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    private ModelMapper modelMapper;

    public CheckoutServiceImpl(BookRepository bookRepository, CheckoutRepository checkoutRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookDTO getBook(Long id) {
        Optional<Book> optionalBook = this.bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            return null;
        }
        Book book = optionalBook.get();
        return modelMapper.map(book, BookDTO.class);
    }
}
