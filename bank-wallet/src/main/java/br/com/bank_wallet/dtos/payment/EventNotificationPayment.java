package br.com.bank_wallet.dtos.payment;

public record EventNotificationPayment(
        String userId,
        String fullName,
        Double money
) {
}
