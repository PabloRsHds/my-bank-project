package br.com.bank_wallet.dtos;

public record EventCreditPayment(
        String userId,
        Double money
) {
}
