package br.com.bank_wallet.dtos.payment;

public record EventCreditPayment(
        String userId,
        Double money
) {
}
