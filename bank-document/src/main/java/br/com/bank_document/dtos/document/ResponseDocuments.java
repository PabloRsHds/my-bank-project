package br.com.bank_document.dtos.document;

import br.com.bank_document.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record ResponseDocuments(
        Long documentId,
        String rg,
        String cpf,
        String addressFile,
        String incomeFile,

        @Enumerated(EnumType.STRING)
        Status status
) {

}
