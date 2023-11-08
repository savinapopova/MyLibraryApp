package com.example.mylibrary.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

   @Column(nullable = false)
    private String author;

   private String image;

   @Column(columnDefinition = "TEXT")
   private String description;

   @Column(nullable = false)
   private int copies;

    @Column(nullable = false)
    private int copiesAvailable;

   @ManyToOne(optional = false)
   private Category category;

    public Book() {
    }
}
