package br.com.bank_card.dtos;

public record ConsumerCreditPayment(
        String userId,
        Double money
) {
}
