package br.com.bank_login.dtos.login;

import jakarta.validation.constraints.NotBlank;

public record RequestLoginDto(

        @NotBlank(message = "Cpf cannot be null")
        String cpf,

        @NotBlank(message = "Password cannot be null")
        String password
) {
}
