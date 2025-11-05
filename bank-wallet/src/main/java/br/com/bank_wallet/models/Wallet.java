package br.com.bank_wallet.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidade que representa uma carteira digital no sistema bancário
 * Armazena informações sobre saldos e vincula usuários a seus fundos
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_wallets")
@Data
public class Wallet {

    /**
     * Identificador único da carteira (chave primária)
     * Gerado automaticamente via estratégia SEQUENCE do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "wallet_id")
    private Long walletId;

    /**
     * ID do usuário proprietário da carteira
     * Valor único que vincula a carteira a um usuário específico
     */
    @Column(unique = true)
    private String userId;

    /**
     * Saldo atual da carteira
     * Representa o valor monetário disponível para transações
     */
    private Double money;
}