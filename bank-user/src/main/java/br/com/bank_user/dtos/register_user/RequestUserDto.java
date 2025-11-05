package br.com.bank_user.dtos.register_user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

//Valor que será enviado
public record RequestUserDto(

        @NotBlank(message = "The cpf field cannot be blank")
        @CPF(message = "This cpf is incorrect")
        String cpf,

        @NotBlank(message = "The fullName field cannot be blank")
        @Size(min = 3, max = 100, message = "The password must be between 3 and 100 characters")
        String fullName,

        @NotBlank(message = "The email field cannot be blank")
        @Size(min = 11, max = 60, message = "The E-mail must have at least 11 characters, and a maximum of 60 characters.")
        @Email(message = "@ is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$",
                message = "Invalid email format. Exemple lara@gmail.com"
        )
        String email,

        @NotBlank(message = "The password field cannot be blank")
        @Size(min = 8, max = 30, message = "The password must be between 8 and 30 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$#!%&])\\S{8,}$",
                message = "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 symbol (no spaces)"
        )
        String password,

        @NotBlank(message = "The fullName field cannot be blank")
        @Size(min = 11, max = 15, message = "The password must be between 11 and 13 characters")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Invalid phone format. Example: (11) 91234-5678"
        )
        String phone,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date
) {
}
