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

    public Book(Long id, String title, String author, String image, String description,
                int copies, int copiesAvailable, Category category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.image = image;
        this.description = description;
        this.copies = copies;
        this.copiesAvailable = copiesAvailable;
        this.category = category;
    }
}
