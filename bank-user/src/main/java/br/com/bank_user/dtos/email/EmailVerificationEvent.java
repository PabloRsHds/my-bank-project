package br.com.bank_user.dtos.email;

//Evento que será enviado ao kafka
public record EmailVerificationEvent(
        String email,
        String code) {
}
