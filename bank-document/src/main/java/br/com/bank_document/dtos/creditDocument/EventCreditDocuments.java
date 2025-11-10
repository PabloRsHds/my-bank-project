package br.com.bank_document.dtos.creditDocument;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record EventCreditDocuments(
        String userId,

        String fullName,

        @NotBlank(message = "The cpf field cannot be blank")
        @CPF(message = "This cpf is incorrect")
        String cpf,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date,

        String occupation,

        Double salary,

        @NotNull(message = "Proof of income cannot be blank")
        String proofOfIncome
) {
}
