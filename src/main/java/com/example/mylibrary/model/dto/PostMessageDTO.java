package com.example.mylibrary.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostMessageDTO {

    @NotBlank(message = "Title cannot be empty!")
    private String title;

    @NotBlank(message = "Question cannot be empty!")
    private String question;

    public PostMessageDTO(String title, String question) {
        this.title = title;
        this.question = question;
    }

    public PostMessageDTO() {
    }

}
