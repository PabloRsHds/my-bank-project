package br.com.bank_card.dtos.card;


public record RequestBlockCard(
        String userId,
        String fullName,
        String cardNumber,
        String expirationDate,
        String cvv) {
}
