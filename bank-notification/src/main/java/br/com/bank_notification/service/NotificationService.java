package br.com.bank_notification.service;

import br.com.bank_notification.dtos.RequestNotificationId;
import br.com.bank_notification.dtos.ResponseNotifications;
import br.com.bank_notification.model.Notification;
import br.com.bank_notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de notificações do sistema bancário
 * Responsável por operações de consulta, ocultação e marcação de notificações
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Construtor para injeção de dependências do repositório de notificações
     *
     * @param notificationRepository Repositório para operações de banco de dados
     */
    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Retorna todas as notificações visíveis do usuário
     * Filtra notificações que devem ser exibidas na tela inicial
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de notificações visíveis
     */
    public ResponseEntity<List<ResponseNotifications>> allNotifications(JwtAuthenticationToken token) {

        List<ResponseNotifications> notifications = this.notificationRepository.findAllByUserId(token.getName()).stream()
                .filter(notification -> Boolean.TRUE.equals(notification.getShowNotification()))
                .map(notification -> new ResponseNotifications(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getShowNotification(),
                        notification.getTimestamp()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    /**
     * Retorna todas as notificações ocultas do usuário
     * Filtra notificações que foram removidas da tela inicial
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de notificações ocultas
     */
    public ResponseEntity<List<ResponseNotifications>> allNotificationsOccult(JwtAuthenticationToken token) {

        List<ResponseNotifications> notifications = this.notificationRepository.findAllByUserId(token.getName()).stream()
                .filter(notification -> Boolean.FALSE.equals(notification.getShowNotification()))
                .map(notification -> new ResponseNotifications(
                        notification.getNotificationId(),
                        notification.getMessage(),
                        notification.getShowNotification(),
                        notification.getTimestamp()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    /**
     * Oculta uma notificação específica da tela inicial
     * Altera o flag showNotification para false
     *
     * @param request DTO contendo o ID da notificação a ser ocultada
     */
    public void occultNotification(RequestNotificationId request) {

        var notification = this.notificationRepository.findById(request.notificationId());

        if (notification.isEmpty()) {
            return;
        }

        notification.get().setShowNotification(false);
        this.notificationRepository.save(notification.get());
    }

    /**
     * Marca todas as notificações do usuário como visualizadas
     * Altera o flag visualisation para true em todas as notificações
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity vazio com status 200
     */
    public ResponseEntity<Void> visualisation(JwtAuthenticationToken token) {

        List<Notification> notifications = this.notificationRepository.findAllByUserId(token.getName());

        for (var notification : notifications) {
            notification.setVisualisation(true);
            this.notificationRepository.save(notification);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Conta o número de notificações não visualizadas do usuário
     * Utilizado para exibir badges ou contadores de notificações
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return Número inteiro representando notificações não visualizadas
     */
    public int countNotifications(JwtAuthenticationToken token) {
        return this.notificationRepository.countByUserIdAndVisualisationFalse(token.getName());
    }
}