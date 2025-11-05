package br.com.bank_document.repositories;

import br.com.bank_document.models.CreditDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações de persistência e consulta de entidades CreditDocument
 * Fornece métodos para gerenciamento de documentos de crédito no sistema
 *
 * @author Pablo R.
 */
public interface CreditDocumentRepository extends JpaRepository<CreditDocument, Long> {

    /**
     * Busca um documento de crédito pelo ID do usuário
     *
     * @param userId ID único do usuário para busca
     * @return Optional contendo o CreditDocument se encontrado, ou Optional vazio
     * @implNote Utiliza consulta derivada do Spring Data JPA
     * @example
     * Optional<CreditDocument> document = creditDocumentRepository.findByUserId("user-123");
     */
    Optional<CreditDocument> findByUserId(String userId);

    /**
     * Remove todos os documentos de crédito associados a um usuário
     *
     * @param userId ID único do usuário para deleção em lote
     * @implNote Método de deleção em lote para limpeza de dados
     * @security Geralmente utilizado em processos de exclusão de conta
     * @example
     * creditDocumentRepository.deleteAllByUserId("user-123");
     */
    void deleteAllByUserId(String userId);
}
