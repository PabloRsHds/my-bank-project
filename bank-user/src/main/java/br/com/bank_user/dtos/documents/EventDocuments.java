package br.com.bank_user.dtos.documents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record EventDocuments(

        String userId,

        String fullName,

        @NotBlank()
        @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}-[\\dXx]$", message = "RG must be in the format 12.345.678-9")
        String rg,

        @NotBlank(message = "The cpf field cannot be blank")
        @CPF(message = "This cpf is incorrect")
        String cpf,

        @NotNull(message = "Proof of address cannot be blank")
        String proofOfAddress,

        @NotNull(message = "Proof of income cannot be blank")
        String proofOfIncome
) {
}
