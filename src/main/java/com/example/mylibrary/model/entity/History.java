package com.example.mylibrary.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "histories")
@Data
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

   @ManyToOne(optional = false)
    private Book book;

    @Column(name = "checkout_date", nullable = false)
    private String checkoutDate;

    @Column(name = "return_date", nullable = false)
    private String returnDate;

    public History(User user, Book book, String checkoutDate, String returnDate) {
        this.user = user;
        this.book = book;
        this.checkoutDate = checkoutDate;
        this.returnDate = returnDate;
    }

    public History() {
    }
}
