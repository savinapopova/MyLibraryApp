package com.example.mylibrary.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostMessageDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String question;

    public PostMessageDTO(String title, String question) {
        this.title = title;
        this.question = question;
    }

    public PostMessageDTO() {
    }

}
