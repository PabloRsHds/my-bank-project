package br.com.bank_login.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ResponseLoginHistory(

        @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm")
        LocalDateTime timeStamp
) {
}
