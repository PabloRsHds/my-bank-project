package br.com.bank_user.dtos.email;

//Record de reenvio de código ao usuario, o valor do e-mail será pego na url
public record ResendCodeDto(
        String email
) {
}
