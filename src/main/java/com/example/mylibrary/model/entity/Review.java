package com.example.mylibrary.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double rating;

    private String comment;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Book book;


    private LocalDate date;

    public Review() {
    }



}
