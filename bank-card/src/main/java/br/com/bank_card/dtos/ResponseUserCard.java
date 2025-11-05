package br.com.bank_card.dtos;
import br.com.bank_card.enums.TypeCard;

public record ResponseUserCard(
        String fullName,
        String cardNumber,
        String expirationDate,
        String cardCvv,
        Double limitCredit,
        TypeCard typeOfCard
) {
}
