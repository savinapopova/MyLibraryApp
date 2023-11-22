package com.example.mylibrary.event;

import com.example.mylibrary.model.entity.Book;
import org.springframework.context.ApplicationEvent;

public class CheckoutCreatedEvent extends ApplicationEvent {

    private Book book;

    public CheckoutCreatedEvent(Object source) {
        super(source);
    }

    public Book getBook() {
        return book;
    }

    public CheckoutCreatedEvent setBook(Book book) {
        this.book = book;
        return this;
    }
}
