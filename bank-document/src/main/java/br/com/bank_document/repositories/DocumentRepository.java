package br.com.bank_document.repositories;

import br.com.bank_document.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência e consulta de entidades Document
 * Fornece métodos para gerenciamento de documentos cadastrais no sistema
 *
 * @author Pablo R.
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Busca um documento cadastral pelo ID do usuário
     *
     * @param userId ID único do usuário para busca
     * @return Optional contendo o Document se encontrado, ou Optional vazio
     * @implNote Retorna o primeiro documento encontrado para o usuário
     * @example
     * Optional<Document> document = documentRepository.findByUserId("user-123");
     */
    Optional<Document> findByUserId(String userId);

    /**
     * Busca todos os documentos cadastrais associados a um usuário
     *
     * @param userId ID único do usuário para busca
     * @return Lista de Documentos do usuário (pode estar vazia)
     * @implNote Útil para histórico de submissões ou múltiplas versões
     * @example
     * List<Document> userDocuments = documentRepository.findAllByUserId("user-123");
     */
    List<Document> findAllByUserId(String userId);

    /**
     * Remove todos os documentos cadastrais associados a um usuário
     *
     * @param userId ID único do usuário para deleção em lote
     * @implNote Método de deleção em lote para limpeza completa de dados documentais
     * @security Utilizado em processos de exclusão de conta ou compliance
     * @example
     * documentRepository.deleteAllByUserId("user-123");
     */
    void deleteAllByUserId(String userId);
}
