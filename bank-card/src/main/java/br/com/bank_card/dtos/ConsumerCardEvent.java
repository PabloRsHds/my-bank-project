package br.com.bank_card.dtos;


public record ConsumerCardEvent(
        String userId,
        String fullName,
        String rg,
        String cpf) {
}
