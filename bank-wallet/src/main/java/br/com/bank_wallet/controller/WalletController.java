package br.com.bank_wallet.controller;

import br.com.bank_wallet.dtos.wallet.ResponseWallet;
import br.com.bank_wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para operações de consulta da carteira digital
 * Expõe endpoints para consulta de saldo e informações da carteira do usuário
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o prefixo base para todos os endpoints
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class WalletController {

    private final WalletService walletService;

    /**
     * Construtor para injeção de dependências do serviço de carteira
     *
     * @param service Serviço com lógica de negócio para consulta de carteiras
     */
    @Autowired
    public WalletController(WalletService service) {
        this.walletService = service;
    }

    /**
     * Endpoint para consulta do saldo da carteira do usuário
     * Retorna informações sobre o saldo disponível para transações
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com DTO contendo o saldo da carteira
     */
    @GetMapping("/get-wallet")
    public ResponseEntity<ResponseWallet> getWallet(JwtAuthenticationToken token) {
        return this.walletService.getWallet(token);
    }
}