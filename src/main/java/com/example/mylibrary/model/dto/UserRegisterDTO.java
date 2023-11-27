package com.example.mylibrary.model.dto;

import com.example.mylibrary.validation.annotation.PasswordMatch;
import com.example.mylibrary.validation.annotation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatch(first = "password", second = "confirmPassword")
public class UserRegisterDTO {

   @NotBlank(message = "Email cannot be empty.")
   @Email(message = "Email must be valid!")
   @UniqueEmail
    private String email;

   @NotBlank(message = "")
   @Size(min = 2, max = 20,  message = "First name length must be between 2 and 20 characters!")
   private String firstName;

    @NotBlank(message = "")
    @Size(min = 2, max = 20,  message = "Last name length must be between 2 and 20 characters!")
    private String lastName;

   @NotBlank(message = "")
   @Size(min = 5, max = 20,  message = "Password length must be between 5 and 20 characters!")
   private String password;

   @NotBlank(message = "")
   @Size(min = 5, max = 20, message = "Password length must be between 5 and 20 characters!")
   private String confirmPassword;

    public UserRegisterDTO() {
    }

    public UserRegisterDTO(String email, String firstName, String lastName, String password, String confirmPassword) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
