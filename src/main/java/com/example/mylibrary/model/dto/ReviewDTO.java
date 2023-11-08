package com.example.mylibrary.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewDTO {

    private double rating;

    private String comment;

    private String userEmail;

    private Date date;

    public ReviewDTO() {
    }
}
