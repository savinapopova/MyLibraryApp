package com.example.mylibrary.model.dto.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageResponseDTO {

    @NotBlank
    private String response;

    public MessageResponseDTO(String response) {
        this.response = response;
    }

    public MessageResponseDTO() {
    }
}
