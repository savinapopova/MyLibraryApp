package com.example.mylibrary.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageResponseDTO {

    @NotBlank
    private String response;
}
