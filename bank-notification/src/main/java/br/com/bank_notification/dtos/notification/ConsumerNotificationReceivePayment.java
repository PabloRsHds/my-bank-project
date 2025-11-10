package br.com.bank_notification.dtos.notification;

public record ConsumerNotificationReceivePayment(
        String userId,
        String fullName,
        Double money
) {
}
