package br.com.bank_user.model;

import br.com.bank_user.enums.Role;
import br.com.bank_user.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário no sistema bancário
 * Mapeada para a tabela 'tb_users' no banco de dados
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_users")
@Data
public class User {

    /**
     * Identificador único do usuário (UUID)
     * Gerado automaticamente pelo banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, name = "user_id")
    private String userId;

    /**
     * Cadastro de Pessoa Física (CPF) do usuário
     * Campo único no sistema
     */
    @Column(unique = true)
    private String cpf;

    /**
     * Nome completo do usuário
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * Endereço de email do usuário
     * Campo único no sistema, utilizado para login
     */
    @Column(unique = true)
    private String email;

    /**
     * Senha do usuário (criptografada)
     * Deve ser armazenada de forma segura
     */
    private String password;

    /**
     * Número de telefone do usuário
     * Campo único no sistema
     */
    @Column(unique = true)
    private String phone;

    /**
     * Data de nascimento do usuário
     * Formatada no padrão brasileiro (dd/MM/yyyy)
     */
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    /**
     * Perfil de acesso do usuário no sistema
     * Define as permissões e acesso (USER, ADMIN)
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Status da conta do usuário
     * Controla se a conta está ATIVA ou BLOQUEADA.
     */
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /**
     * Indica se o usuário completou a autenticação como cliente
     * Valor padrão: false
     */
    private Boolean authenticatedClient = false;

    /**
     * Data e hora de criação do registro do usuário
     * Preenchida automaticamente pelo Hibernate
     */
    @CreationTimestamp
    private LocalDateTime timesTamp;

    /**
     * Código de verificação de email
     * Gerado para validação de conta por email
     */
    private String code;

    /**
     * Data e hora de expiração do código de verificação
     * Controla a validade do código de verificação
     */
    private LocalDateTime expireCode;

    /**
     * Indica se o email do usuário foi verificado
     * Valor padrão: false
     * Torna-se true após verificação bem-sucedida
     */
    private Boolean verifyEmail = false;
}
