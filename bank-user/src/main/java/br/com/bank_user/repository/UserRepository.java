package br.com.bank_user.repository;

import br.com.bank_user.model.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositório para operações de persistência e consulta de entidades User
 * Estende JpaRepository fornecendo operações CRUD padrão
 *
 * @author Pablo R.
 */
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Consulta personalizada para buscar usuário por múltiplos identificadores
     * Realiza busca flexível usando ID, CPF, telefone ou email como critério
     *
     * @param key Chave de pesquisa que pode ser:
     *           - ID único do usuário
     *           - CPF (formato: 123.456.789-00)
     *           - Número de telefone
     *           - Endereço de email (case insensitive)
     *
     * @return Optional contendo o usuário se encontrado por qualquer um dos critérios
     */
    @Query("""
        SELECT u FROM User u
        WHERE u.userId = :key
           OR u.cpf = :key
           OR u.phone = :key
           OR LOWER(u.email) = LOWER(:key)
    """)
    Optional<User> findByUserWithIdOrCpfOrPhoneOrEmail(@Param("key") String key);

    /**
     * Busca um usuário pelo número do CPF
     *
     * @param cpf Número do CPF para busca (formato: 000.000.000-00)
     * @return Optional contendo o User se encontrado, ou Optional vazio
     */
    Optional<User> findByCpf(String cpf);


    /**
     * Busca um usuário pelo endereço de email
     *
     * @param email Endereço de email para busca
     * @return Optional contendo o User se encontrado, ou Optional vazio
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário pelo número de telefone
     *
     * @param phone Número de telefone para busca (formato: DDXXXXXXXXX)
     * @return Optional contendo o User se encontrado, ou Optional vazio
     */
    Optional<User> findByPhone(String phone);

    /**
     * Remove usuários não verificados criados antes da data limite
     * Operação de limpeza para manter a base de dados otimizada
     *
     * @param cutoff Data e hora limite para exclusão
     */
    @Modifying
    @Query("DELETE FROM User u WHERE u.verifyEmail = false AND u.timesTamp < :cutoff")
    void deleteUnverifiedUsersBefore(@Param("cutoff") LocalDateTime cutoff);
}
