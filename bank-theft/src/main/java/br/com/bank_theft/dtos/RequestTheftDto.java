package br.com.bank_theft.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestTheftDto(

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dateOfTheft,

        @JsonFormat(pattern = "HH:mm")
        LocalTime timeOfTheft,

        @NotBlank(message = "Location of theft is required")
        String locationOfTheft,

        @NotBlank(message = "Transaction ID is required")
        String transactionId,

        @NotNull(message = "Amount lost is required")
        Double amountLost,

        @NotBlank(message = "Description is required")
        String description
) {
}
