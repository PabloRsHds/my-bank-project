package br.com.bank_wallet.service;

import br.com.bank_wallet.dtos.ResponseWallet;
import br.com.bank_wallet.models.Wallet;
import br.com.bank_wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço para consulta de informações da carteira digital do usuário
 * Responsável por operações de consulta de saldo e dados da carteira
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * Construtor para injeção de dependências do repositório de carteiras
     *
     * @param repository Repositório para operações de banco de dados de carteiras
     */
    @Autowired
    public WalletService(WalletRepository repository) {
        this.walletRepository = repository;
    }

    /**
     * Recupera as informações da carteira do usuário autenticado
     * Retorna o saldo atual disponível para transações
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com DTO contendo o saldo da carteira
     */
    public ResponseEntity<ResponseWallet> getWallet(JwtAuthenticationToken token) {

        Optional<Wallet> wallet = this.walletRepository.findByUserId(token.getName());

        return wallet.map(value -> ResponseEntity.ok(new ResponseWallet(value.getMoney())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}