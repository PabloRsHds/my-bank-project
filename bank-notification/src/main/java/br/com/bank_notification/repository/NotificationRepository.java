package br.com.bank_notification.repository;

import br.com.bank_notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório para operações de persistência e consulta de entidades Notification
 * Fornece métodos para gerenciamento de notificações no sistema
 *
 * @author Pablo R.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Busca todas as notificações de um usuário específico
     *
     * @param userId ID do usuário para busca
     * @return Lista de notificações do usuário (pode estar vazia)
     */
    List<Notification> findAllByUserId(String userId);

    /**
     * Conta o número de notificações não visualizadas de um usuário
     *
     * @param userId ID do usuário para contagem
     * @return Número de notificações não visualizadas
     */
    int countByUserIdAndVisualisationFalse(String userId);

    /**
     * Remove todas as notificações associadas a um usuário
     *
     * @param userId ID do usuário para deleção em lote
     */
    void deleteAllByUserId(String userId);
}