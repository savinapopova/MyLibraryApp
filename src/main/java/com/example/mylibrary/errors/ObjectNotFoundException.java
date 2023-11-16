package com.example.mylibrary.errors;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Data
public class ObjectNotFoundException extends RuntimeException {

    private String massage;

    public ObjectNotFoundException(String message) {
        this.massage = message;
    }


}