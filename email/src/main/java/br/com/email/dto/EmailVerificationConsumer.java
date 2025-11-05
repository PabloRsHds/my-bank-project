package br.com.email.dto;

public record EmailVerificationConsumer(
        String email,
        String code
) {
}
