package br.com.bank_login.dtos;

public record ResponseTokens(
    String accessToken,
    String refreshToken
) {
}
