package br.com.bank_wallet.models;

import br.com.bank_wallet.enums.PixOrCredit;
import br.com.bank_wallet.enums.SendOrReceive;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa um pagamento no sistema bancário
 * Armazena informações sobre transações financeiras entre usuários
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_payments")
@Data
public class Payment {

    /**
     * Identificador único do pagamento (chave primária)
     * Gerado automaticamente via estratégia SEQUENCE do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "payment_id")
    private Long paymentId;

    /**
     * ID do usuário remetente da transação
     * Identifica quem enviou o pagamento
     */
    private String userSend;

    /**
     * ID do usuário destinatário da transação
     * Identifica quem recebeu o pagamento
     */
    private String userReceive;

    /**
     * Valor monetário da transação
     * Representa a quantia transferida entre os usuários
     */
    private Double money;

    /**
     * Tipo de operação (envio ou recebimento)
     * Classifica se a transação foi de envio ou recebimento para o usuário atual
     */
    @Enumerated(EnumType.STRING)
    private SendOrReceive sendOrReceive;

    /**
     * Método de pagamento utilizado
     * Especifica se a transação foi via PIX ou Cartão de Crédito
     */
    @Enumerated(EnumType.STRING)
    private PixOrCredit pixOrCredit;

    /**
     * Data e hora da transação
     * Preenchida automaticamente pelo Hibernate na criação do registro
     * Formatada no padrão ano-mês-dia hora:minuto
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm")
    private LocalDateTime timeStamp;
}