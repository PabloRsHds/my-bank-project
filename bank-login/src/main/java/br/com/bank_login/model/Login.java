package br.com.bank_login.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa um registro de login no sistema bancário
 * Armazena informações sobre quando usuários acessam o sistema
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_logins")
@Data
public class Login {

    /**
     * Identificador único do registro de login (chave primária)
     * Gerado automaticamente via estratégia AUTO do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "login_id")
    private Long loginId;

    /**
     * ID do usuário que realizou o login
     * Relaciona o registro de login com o usuário no sistema
     */
    private String userId;

    /**
     * Data e hora do login no sistema
     * Preenchida automaticamente pelo Hibernate na criação do registro
     * Formatada no padrão ano-mês-dia hora:minuto
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm")
    private LocalDateTime timeStamp;
}