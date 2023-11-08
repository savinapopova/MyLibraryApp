package com.example.mylibrary.model.dto;

import lombok.Data;

@Data
public class SearchBookDTO {

    private Long id;

    private String title;

    private String author;

    private String image;

    private String description;

    public SearchBookDTO() {
    }
}
