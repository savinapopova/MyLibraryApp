package com.example.mylibrary.model.dto.review;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LeaveReviewDTO {

    @NotNull(message = "Rating is required!")
    private Double rating;

    private String comment;

    public LeaveReviewDTO(double rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public LeaveReviewDTO() {
    }

    public LeaveReviewDTO(Double rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
