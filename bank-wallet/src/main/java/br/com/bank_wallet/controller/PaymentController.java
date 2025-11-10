package br.com.bank_wallet.controller;

import br.com.bank_wallet.dtos.payment.RequestCreditPayment;
import br.com.bank_wallet.dtos.payment.RequestPayment;
import br.com.bank_wallet.dtos.payment.ResponsePayments;
import br.com.bank_wallet.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operações de pagamentos e transações financeiras
 * Expõe endpoints para envio de pagamentos, quitação de crédito e consulta de histórico
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o prefixo base para todos os endpoints
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Construtor para injeção de dependências do serviço de pagamentos
     *
     * @param service Serviço com lógica de negócio para transações financeiras
     */
    @Autowired
    public PaymentController(PaymentService service) {
        this.paymentService = service;
    }

    /**
     * Endpoint para realização de pagamentos entre usuários
     * Suporta transações via PIX e Cartão de Crédito
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @param request DTO com dados do pagamento (valor, chave destino, método)
     * @return ResponseEntity com resultado da operação
     */
    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> payment(
            JwtAuthenticationToken token,@RequestBody RequestPayment request) {
        return this.paymentService.payment(token, request);
    }

    /**
     * Endpoint para pagamento de fatura do cartão de crédito
     * Utiliza saldo da carteira PIX para quitar débitos de crédito
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @param request DTO com valor do pagamento para quitação
     * @return ResponseEntity com resultado da operação
     */
    @PostMapping("/credit-payment")
    public ResponseEntity<Map<String, String>> creditPayment(
            JwtAuthenticationToken token,@RequestBody RequestCreditPayment request){
        return this.paymentService.creditPayment(token, request);
    }

    /**
     * Endpoint para consulta de pagamentos enviados pelo usuário
     * Retorna histórico de transações de envio ordenadas por data
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de pagamentos enviados
     */
    @GetMapping("/get-send-payments")
    public ResponseEntity<List<ResponsePayments>> getAllSendPaymentsForUser(JwtAuthenticationToken token) {
        return this.paymentService.getAllSendPaymentsForUser(token);
    }

    /**
     * Endpoint para consulta de pagamentos recebidos pelo usuário
     * Retorna histórico de transações de recebimento ordenadas por data
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de pagamentos recebidos
     */
    @GetMapping("/get-receive-payments")
    public ResponseEntity<List<ResponsePayments>> getAllReceivePaymentsForUser(JwtAuthenticationToken token) {
        return this.paymentService.getAllReceivePaymentsForUser(token);
    }
}