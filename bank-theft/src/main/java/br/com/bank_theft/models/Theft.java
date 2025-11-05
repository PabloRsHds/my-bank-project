package br.com.bank_theft.models;

import br.com.bank_theft.enums.StatusOfReport;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidade que representa um registro de roubo ou furto no sistema bancário
 * Armazena informações detalhadas sobre incidentes de segurança relatados pelos usuários
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 * @noArgsConstructor Cria construtor vazio para JPA
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_thefts")
@Data
@NoArgsConstructor
public class Theft {

    /**
     * Identificador único do registro de roubo (chave primária)
     * Gerado automaticamente via estratégia IDENTITY do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Data em que o roubo/furto ocorreu
     * Formatada no padrão brasileiro (dd/MM/yyyy)
     */
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfTheft;

    /**
     * Horário em que o roubo/furto ocorreu
     * Formatado no padrão brasileiro (HH:mm)
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime timeOfTheft;

    /**
     * Local onde o roubo/furto ocorreu
     * Descrição textual do local do incidente
     */
    private String locationOfTheft;

    /**
     * ID da transação relacionada ao roubo, se aplicável
     * Utilizado para correlacionar com o sistema de transações
     */
    private String transactionId;

    /**
     * Valor monetário perdido no incidente
     * Representa o prejuízo financeiro do usuário
     */
    private Double amountLost;

    /**
     * Descrição detalhada do incidente
     * Relato completo do ocorrido pelo usuário
     */
    private String description;

    /**
     * Status atual do relato de roubo
     * Controla o fluxo de análise e tratamento do caso
     */
    @Enumerated(EnumType.STRING)
    private StatusOfReport status;

    /**
     * Data de criação do registro no sistema
     * Preenchida automaticamente pelo Hibernate na criação
     * Representa quando o usuário registrou a ocorrência
     */
    @CreationTimestamp
    private LocalDate timestampOfTheft;
}