package br.com.bank_wallet.repositories;

import br.com.bank_wallet.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Wallet
 * Fornece métodos para consulta e manipulação de carteiras digitais dos usuários
 *
 * @repository Interface de repositório Spring Data JPA
 * @author Pablo R.
 */
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Busca uma carteira pelo ID do usuário
     * Retorna a carteira associada ao usuário específico
     *
     * @param userId ID do usuário para busca da carteira
     * @return Optional contendo a carteira se encontrada
     */
    Optional<Wallet> findByUserId(String userId);
}
