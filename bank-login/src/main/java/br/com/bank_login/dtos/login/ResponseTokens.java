package br.com.bank_login.dtos.login;

public record ResponseTokens(
    String accessToken,
    String refreshToken
) {
}
