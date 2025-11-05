package br.com.bank_wallet.dtos;

public record EventNotificationPayment(
        String userId,
        String fullName,
        Double money
) {
}
