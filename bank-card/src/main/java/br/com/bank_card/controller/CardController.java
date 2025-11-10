package br.com.bank_card.controller;

import br.com.bank_card.dtos.card.ResponseUserCard;
import br.com.bank_card.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;


/**
 * Controlador REST para operações de cartões bancários
 * Expõe endpoints para consulta, bloqueio e verificação de cartões dos usuários
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o prefixo base para todos os endpoints
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;

    /**
     * Construtor para injeção de dependências do serviço de cartões
     *
     * @param service Serviço com lógica de negócio para operações com cartões
     */
    @Autowired
    public CardController(CardService service) {
        this.cardService = service;
    }

    /**
     * Endpoint para verificar se o usuário possui cartão e seu status atual
     * Utilizado para determinar o estado do cartão na interface do usuário
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com string representando o status do cartão
     */
    @GetMapping("/verify-if-user-has-card-and-your-status")
    public ResponseEntity<String> verifyIfUserHasCardAndYourStatus(JwtAuthenticationToken token) {
        return cardService.verifyIfUserHasCardAndYourStatus(token);
    }

    /**
     * Endpoint para consulta dos dados completos do cartão do usuário
     * Retorna informações sensíveis como número do cartão, CVV e limite
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com DTO contendo dados completos do cartão
     */
    @GetMapping("/get-user-card")
    public ResponseEntity<ResponseUserCard> getUserCard(JwtAuthenticationToken token) {
        return cardService.getUserCard(token);
    }

    /**
     * Endpoint para alternar o status de bloqueio do cartão
     * Se estiver aprovado, bloqueia; se estiver bloqueado, aprova
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     */
    @PutMapping("/block-card")
    public void blockCard(JwtAuthenticationToken token) {
        this.cardService.blockCard(token);
    }

    /**
     * Endpoint para consulta do limite de crédito disponível
     * Retorna o valor atual do limite do cartão de crédito
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com o valor do limite de crédito
     */
    @GetMapping("/get-limit-credit")
    public ResponseEntity<Double> getLimitOfCredit(JwtAuthenticationToken token) {
        return this.cardService.getLimitOfCredit(token);
    }
}