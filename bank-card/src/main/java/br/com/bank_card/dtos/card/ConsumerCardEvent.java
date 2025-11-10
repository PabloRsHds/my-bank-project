package br.com.bank_card.dtos.card;


public record ConsumerCardEvent(
        String userId,
        String fullName,
        String rg,
        String cpf) {
}
