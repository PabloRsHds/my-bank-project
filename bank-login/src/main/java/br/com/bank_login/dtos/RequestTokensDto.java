package br.com.bank_login.dtos;

public record RequestTokensDto(
    String accessToken,
    String refreshToken
) {
}
