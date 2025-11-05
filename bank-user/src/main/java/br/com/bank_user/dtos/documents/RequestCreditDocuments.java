package br.com.bank_user.dtos.documents;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record RequestCreditDocuments(

        String fullName,

        @NotBlank(message = "The cpf field cannot be blank")
        @CPF(message = "This cpf is incorrect")
        String cpf,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date,

        String occupation,

        Double salary,

        MultipartFile proofOfIncome
) {
}
