package com.example.mylibrary.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "messages")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String question;

    @ManyToOne
    private User user;

    @ManyToOne
    private User admin;

    private String response;

    private boolean closed;




    public Message(String title, String question) {
        this.title = title;
        this.question = question;
    }

    public Message() {
    }

}
