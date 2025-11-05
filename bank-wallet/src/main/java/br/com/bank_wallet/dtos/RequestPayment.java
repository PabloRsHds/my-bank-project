package br.com.bank_wallet.dtos;
import br.com.bank_wallet.enums.PixOrCredit;

public record RequestPayment(
        Double money,
        String key,
        PixOrCredit pixOrCredit
) {
}
