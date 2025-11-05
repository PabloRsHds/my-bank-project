package br.com.bank_document.models;

import br.com.bank_document.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * Entidade que representa documentos para análise de crédito no sistema bancário
 * Armazena informações financeiras e profissionais para solicitação de limite de crédito
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_credit_documents")
@Data
public class CreditDocument {

    /**
     * Identificador único do documento de crédito (chave primária)
     * Gerado automaticamente via estratégia AUTO do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, name = "credit_document_id")
    private Long creditDocumentId;

    /**
     * ID do usuário que solicitou a análise de crédito
     * Relaciona o documento com o usuário no sistema
     */
    private String userId;

    /**
     * Nome completo do usuário conforme documentos
     */
    private String fullName;

    /**
     * Cadastro de Pessoa Física (CPF) do usuário
     * Documento de identificação fiscal para análise cadastral
     */
    private String cpf;

    /**
     * Data de referência para a análise de crédito
     * Formatada no padrão brasileiro (dd/MM/yyyy)
     */
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    /**
     * Profissão ou ocupação do usuário
     * Utilizado para avaliação de estabilidade profissional
     */
    private String occupation;

    /**
     * Renda mensal do usuário
     * Valor utilizado como base para cálculo do limite de crédito
     */
    private Double salary;

    /**
     * Caminho ou referência para o arquivo de comprovante de renda
     * Armazena a localização do arquivo que comprova a renda declarada
     */
    private String incomeFile;

    /**
     * Status atual da solicitação de crédito
     * Controla o fluxo de análise e aprovação do limite de crédito
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Data de criação do registro da solicitação de crédito
     * Preenchida automaticamente pelo Hibernate na criação
     */
    @CreationTimestamp
    private LocalDate timeStamp;
}
