package br.com.bank_document.dtos;


public record SendCardEvent(
    String userId,
    String fullName,
    String rg,
    String cpf) {
}
