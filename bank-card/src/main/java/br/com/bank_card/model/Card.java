package br.com.bank_card.model;

import br.com.bank_card.enums.Status;
import br.com.bank_card.enums.TypeCard;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * Entidade que representa um cartão no sistema bancário
 * Armazena informações completas sobre cartões de crédito/débito dos usuários
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_cards")
@Data
public class Card {

    /**
     * Identificador único do cartão (chave primária)
     * Gerado automaticamente via estratégia UUID do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "card_id")
    private String cardId;

    /**
     * ID do usuário proprietário do cartão
     * Relaciona o cartão com o usuário no sistema
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * Nome completo do titular do cartão
     * Como registrado no documento de identificação
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * Número do Registro Geral do titular
     * Documento de identificação civil
     */
    private String rg;

    /**
     * Cadastro de Pessoa Física do titular
     * Documento de identificação fiscal
     */
    private String cpf;

    /**
     * Número do cartão de crédito/débito
     * Sequência numérica única que identifica o cartão
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * Limite de crédito disponível no cartão
     * Valor máximo que pode ser utilizado em compras a crédito
     */
    private Double limitCredit = null;

    /**
     * Data de expiração do cartão
     * Período de validade do cartão no formato MM/AA
     */
    private String expirationDate;

    /**
     * Código de segurança do cartão
     * Número de verificação de 3 dígitos para transações
     */
    private String cardCvv;

    /**
     * Tipo do cartão (Crédito ou Débito)
     * Classifica a funcionalidade principal do cartão
     */
    @Enumerated(EnumType.STRING)
    private TypeCard typeOfCard;

    /**
     * Status atual do cartão no sistema
     * Controla se o cartão está ativo, bloqueado ou cancelado
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Data de criação do registro do cartão
     * Preenchida automaticamente pelo Hibernate na criação
     */
    @CreationTimestamp
    private LocalDate timestamp;
}