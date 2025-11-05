package br.com.bank_notification.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

/**
 * Entidade que representa uma notificação no sistema bancário
 * Armazena mensagens e alertas enviados aos usuários do sistema
 *
 * @entity Indica que esta classe é uma entidade JPA
 * @table Especifica o nome da tabela no banco de dados
 * @data Lombok - gera getters, setters, equals, hashCode e toString
 *
 * @author Pablo R.
 */
@Entity
@Table(name = "tb_notifications")
@Data
public class Notification {

    /**
     * Identificador único da notificação (chave primária)
     * Gerado automaticamente via estratégia AUTO do banco de dados
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long notificationId;

    /**
     * ID do usuário destinatário da notificação
     * Relaciona a notificação com o usuário no sistema
     */
    private String userId;

    /**
     * Conteúdo da mensagem da notificação
     * Texto que será exibido para o usuário
     */
    private String message;

    /**
     * Indica se a notificação foi visualizada pelo usuário
     * Valor padrão: false (não visualizada)
     */
    private Boolean visualisation = false;

    /**
     * Controla se a notificação deve ser exibida para o usuário
     * Valor padrão: true (exibir notificação)
     * Pode ser usado para arquivar ou ocultar notificações
     */
    private Boolean showNotification = true;

    /**
     * Data de criação da notificação
     * Preenchida automaticamente pelo Hibernate na criação
     */
    @CreationTimestamp
    private LocalDate timestamp;
}