package com.example.mylibrary.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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
    private LocalDate checkoutDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    public History(Checkout checkout) {
        this.user = checkout.getUser();
        this.book = checkout.getBook();
        this.checkoutDate = checkout.getCheckoutDate();
        this.returnDate = LocalDate.now();
    }

    public History() {
    }
}
