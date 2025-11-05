package br.com.bank_wallet.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente Feign para comunicação com o microserviço de cartões
 * Realiza chamadas HTTP para operações de pagamento com cartão de crédito
 *
 * @feignClient Indica que esta interface é um cliente Feign para o serviço BANK-CARD
 *
 * @author Pablo R.
 */
@FeignClient(name = "BANK-CARD")
public interface CardClient {

    /**
     * Realiza um pagamento utilizando cartão de crédito
     * Processa transações de débito no limite do cartão do usuário
     *
     * @param userId ID único do usuário para processamento do pagamento
     * @param money Valor monetário da transação a ser debitada
     * @return String com resultado da operação (sucesso ou mensagem de erro)
     */
    @PutMapping("/microservice/bank_card/payment-with-credit")
    String paymentWithCredit(@RequestParam String userId, @RequestParam Double money);
}