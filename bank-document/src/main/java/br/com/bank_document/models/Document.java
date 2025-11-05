package br.com.bank_document.models;

import br.com.bank_document.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * Entidade que representa um documento submetido para análise no sistema bancário
 * Armazena informações e arquivos de documentos enviados pelos usuários
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 * @noArgsConstructor Cria construtor vazio para JPA
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_documents")
@Data
@NoArgsConstructor
public class Document {

    /**
     * Identificador único do documento (chave primária)
     * Gerado automaticamente via estratégia IDENTITY do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name = "document_id")
    private Long documentId;

    /**
     * ID do usuário que submeteu o documento
     * Relaciona o documento com o usuário no sistema
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * Nome completo do usuário conforme documentos
     */
    private String fullName;

    /**
     * Registro Geral (RG) do usuário
     * Documento de identificação civil
     */
    private String rg;

    /**
     * Cadastro de Pessoa Física (CPF) do usuário
     * Documento de identificação fiscal
     */
    private String cpf;

    /**
     * Caminho ou referência para o arquivo de comprovante de endereço
     * Armazena a localização do arquivo no sistema de arquivos ou storage
     */
    private String addressFile;

    /**
     * Caminho ou referência para o arquivo de comprovante de renda
     * Armazena a localização do arquivo no sistema de arquivos ou storage
     */
    private String incomeFile;

    /**
     * Status atual do documento no processo de análise
     * Controla o fluxo de aprovação/rejeição dos documentos
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Data de criação do registro do documento
     * Preenchida automaticamente pelo Hibernate na criação
     */
    @CreationTimestamp
    private LocalDate timeStamp;
}
