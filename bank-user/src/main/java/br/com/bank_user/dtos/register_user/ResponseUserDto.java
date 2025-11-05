package br.com.bank_user.dtos.register_user;

//Valor que será retornado
public record ResponseUserDto(
        String cpf,
        String fullName,
        String email,
        String password,
        String phone,
        String date
) {
}
