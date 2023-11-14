package com.example.mylibrary.model.dto;

import com.example.mylibrary.model.enums.CategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AddBookDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotBlank
    private String image;

    @PositiveOrZero
    private int copies;

    public AddBookDTO() {
    }
}
