package br.com.bank_document.dtos.creditDocument;

import br.com.bank_document.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

public record ResponseCreditDocuments(

        Long creditDocumentId,
        String cpf,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date,

        String occupation,
        Double salary,
        String incomeFile,



        @Enumerated(EnumType.STRING)
        Status status
) {
}
