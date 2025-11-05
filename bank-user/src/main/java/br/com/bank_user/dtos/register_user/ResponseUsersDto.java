package br.com.bank_user.dtos.register_user;

public record ResponseUsersDto(
        String userId,
        String cpf,
        String fullName,
        String email,
        String phone,
        String date,
        String role,
        String authenticatedClient,
        String status
) {
}
