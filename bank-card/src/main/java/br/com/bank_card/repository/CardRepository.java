package br.com.bank_card.repository;

import br.com.bank_card.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Card
 * Fornece métodos para consulta e manipulação de registros de cartões
 *
 * @repository Interface de repositório Spring Data JPA
 * @author Pablo R.
 */
public interface CardRepository extends JpaRepository<Card, String> {

    /**
     * Busca um cartão pelo ID do usuário
     * Retorna o cartão associado ao usuário específico
     *
     * @param userId ID do usuário para busca do cartão
     * @return Optional contendo o cartão se encontrado
     */
    Optional<Card> findByUserId(String userId);

    /**
     * Exclui todos os cartões associados a um usuário
     * Utilizado durante processos de exclusão de conta ou limpeza de dados
     *
     * @param userId ID do usuário para exclusão dos cartões
     */
    void deleteAllByUserId(String userId);
}