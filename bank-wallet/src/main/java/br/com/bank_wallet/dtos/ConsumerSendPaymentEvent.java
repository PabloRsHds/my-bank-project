package br.com.bank_wallet.dtos;
import br.com.bank_wallet.enums.PixOrCredit;

public record ConsumerSendPaymentEvent(

        String userSend,
        String userReceive,
        Double money,
        PixOrCredit pixOrCredit
) {
}
