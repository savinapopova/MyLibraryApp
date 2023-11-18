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

    public AddBookDTO(String title, String author, String description, String category, String image, int copies) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.image = image;
        this.copies = copies;
    }
}
