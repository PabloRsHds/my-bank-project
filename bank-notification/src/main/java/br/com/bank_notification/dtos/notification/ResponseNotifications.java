package br.com.bank_notification.dtos.notification;

import java.time.LocalDate;

public record ResponseNotifications(
        Long notificationId,
        String message,
        Boolean showNotification,
        LocalDate timestamp
) {
}
