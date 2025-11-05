package br.com.bank_user.dtos.update_user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RequestPhoneUpdate(
        @NotBlank(message = "The phone field cannot be blank")
        @Size(min = 11, max = 15, message = "The password must be between 11 and 13 characters")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Invalid phone format. Example: (11) 91234-5678"
        )
        String phone
) {
}
