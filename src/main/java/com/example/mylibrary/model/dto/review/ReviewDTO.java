package com.example.mylibrary.model.dto.review;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ReviewDTO {

    private double rating;

    private String comment;

    private String userEmail;


    private LocalDate date;

    public ReviewDTO() {
    }
}
