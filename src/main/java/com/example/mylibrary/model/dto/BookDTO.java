package com.example.mylibrary.model.dto;

import lombok.Data;

@Data
public class BookDTO {

    private Long id;

    private String title;

    private String author;

    private String image;

    private String description;

    private int copies;

    private int copiesAvailable;

    public BookDTO() {
    }
}
