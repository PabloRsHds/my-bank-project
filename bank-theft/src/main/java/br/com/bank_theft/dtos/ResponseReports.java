package br.com.bank_theft.dtos;

public record ResponseReports(
        String dateOfTheft,
        String timeOfTheft,
        String locationOfTheft,
        String transactionId,
        Double amountLost,
        String description,
        String timestampOfTheft,
        String status
) {
}
