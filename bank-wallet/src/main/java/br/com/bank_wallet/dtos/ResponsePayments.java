package br.com.bank_wallet.dtos;

import br.com.bank_wallet.enums.SendOrReceive;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public record ResponsePayments(

        String userSend,
        String userReceive,

        Double money,

        @Enumerated(EnumType.STRING)
        SendOrReceive sendOrReceive,

        @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm")
        LocalDateTime timeStamp

) {
}
