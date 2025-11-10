package br.com.bank_login.repository;

import br.com.bank_login.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Login
 * Fornece métodos para consulta e manipulação de registros de acesso ao sistema
 *
 * @repository Interface de repositório Spring Data JPA
 *
 * @author Pablo R.
 */
public interface LoginRepository extends JpaRepository<Login, Long> {

    /**
     * Busca todos os registros de login de um usuário específico
     * Retorna lista completa do histórico de acessos do usuário
     *
     * @param userId ID do usuário para busca
     * @return Lista de todos os registros de login do usuário
     */
    List<Login> findAllByUserId(String userId);

    /**
     * Exclui todos os registros de login associados a um usuário
     * Utilizado durante processos de exclusão de conta ou limpeza de dados
     *
     * @param userId ID do usuário para exclusão dos registros
     */
    void deleteAllByUserId(String userId);
}