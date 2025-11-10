package br.com.bank_login.dtos.login;

public record RequestTokensDto(
    String accessToken,
    String refreshToken
) {
}
