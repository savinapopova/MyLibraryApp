package com.example.mylibrary.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "checkouts")
@Data
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="checkout_date", nullable = false)
    private LocalDate checkoutDate;

    @Column(name="return_date", nullable = false)
    private LocalDate returnDate;

    @ManyToOne(optional = false)
    private Book book;

    @ManyToOne(optional = false)
    private User user;

    public Checkout(Book book, User user) {
        this.book = book;
        this.user = user;
        this.checkoutDate = LocalDate.now();
        this.returnDate = LocalDate.now().plusDays(30);
    }

    public Checkout() {
    }
}
