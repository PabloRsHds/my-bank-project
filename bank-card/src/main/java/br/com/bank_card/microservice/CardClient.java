package br.com.bank_card.microservice;

import br.com.bank_card.model.Card;
import br.com.bank_card.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controlador REST para operações de microserviço de cartões
 * Expõe endpoints para comunicação interna entre serviços do sistema bancário
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o prefixo base para endpoints de microserviço
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/microservice/bank_card")
public class CardClient {

    private final CardRepository cardRepository;

    /**
     * Construtor para injeção de dependências do repositório de cartões
     *
     * @param cardRepository Repositório para operações de banco de dados de cartões
     */
    @Autowired
    public  CardClient(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Endpoint para processamento de pagamentos com cartão de crédito
     * Realiza débito no limite do cartão incluindo taxa de 5% sobre o valor
     *
     * @param userId ID do usuário para identificação do cartão
     * @param money Valor base da transação a ser processada
     * @return String com resultado da operação:
     *         "OK" - Pagamento processado com sucesso
     *         "INSUFICIENTE" - Limite de crédito insuficiente
     *         null - Cartão não encontrado para o usuário
     *
     * @implNote O valor total debitado inclui uma taxa de 5% sobre o valor original:
     *           valor_total = valor + (valor * 0.05)
     */
    @PutMapping("/payment-with-credit")
    public String paymentWithCredit(@RequestParam String userId, @RequestParam Double money) {

        Optional<Card> card = this.cardRepository.findByUserId(userId);

        if (card.isEmpty()) {
            return null;
        } else if (card.get().getLimitCredit() < ((0.05 * money) + money)) {
            return "INSUFICIENTE";
        }

        card.get().setLimitCredit(card.get().getLimitCredit() - ((0.05 * money) + money));
        this.cardRepository.save(card.get());
        return "OK";
    }
}