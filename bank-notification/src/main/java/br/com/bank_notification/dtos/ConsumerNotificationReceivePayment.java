package br.com.bank_notification.dtos;

public record ConsumerNotificationReceivePayment(
        String userId,
        String fullName,
        Double money
) {
}
