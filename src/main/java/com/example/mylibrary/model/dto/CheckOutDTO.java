package com.example.mylibrary.model.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;


public class CheckOutDTO {

    private SearchBookDTO book;

    private LocalDate checkoutDate;

    private LocalDate returnDate;

    private long daysLeft;

    public CheckOutDTO() {
    }

    public SearchBookDTO getBook() {
        return book;
    }

    public CheckOutDTO setBook(SearchBookDTO book) {
        this.book = book;
        return this;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public CheckOutDTO setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
        return this;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public CheckOutDTO setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        return this;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public CheckOutDTO setDaysLeft(long daysLeft) {
        this.daysLeft = daysLeft;
        return this;
    }
}
