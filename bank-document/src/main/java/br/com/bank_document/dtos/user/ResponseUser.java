package br.com.bank_document.dtos.user;

import java.time.LocalDate;

public record ResponseUser(
        String cpf,
        String fullName,
        String email,
        String password,
        String phone,
        LocalDate date,
        String role,
        String status,
        Boolean authenticatedClient,
        Boolean verifyEmail) {
}
