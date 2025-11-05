package br.com.bank_notification.controller;

import br.com.bank_notification.dtos.RequestNotificationId;
import br.com.bank_notification.dtos.ResponseNotifications;
import br.com.bank_notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciamento de notificações do sistema bancário
 * Expõe endpoints para operações de consulta, ocultação e contagem de notificações
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o prefixo base para todos os endpoints
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Construtor para injeção de dependências do serviço de notificações
     *
     * @param notificationService Serviço com lógica de negócio para notificações
     */
    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Endpoint para recuperar todas as notificações visíveis do usuário
     * Retorna apenas notificações marcadas para exibição na tela inicial
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de notificações visíveis
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<ResponseNotifications>> allNotifications(JwtAuthenticationToken token){
        return this.notificationService.allNotifications(token);
    }

    /**
     * Endpoint para recuperar todas as notificações ocultas do usuário
     * Retorna notificações que foram removidas da visualização principal
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de notificações ocultas
     */
    @GetMapping("/notifications-occult")
    public ResponseEntity<List<ResponseNotifications>> allNotificationsOccult(JwtAuthenticationToken token){
        return this.notificationService.allNotificationsOccult(token);
    }

    /**
     * Endpoint para ocultar uma notificação específica
     * Remove a notificação da tela inicial mantendo-a no histórico
     *
     * @param request DTO contendo o ID da notificação a ser ocultada
     */
    @PostMapping("/occult-notification")
    public void occultNotification(@RequestBody RequestNotificationId request) {
        this.notificationService.occultNotification(request);
    }

    /**
     * Endpoint para marcar todas as notificações como visualizadas
     * Atualiza o status de visualização em lote para o usuário
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity vazio com status 200
     */
    @PutMapping("/visualisation-notification")
    public ResponseEntity<Void> visualisation(JwtAuthenticationToken token) {
        return this.notificationService.visualisation(token);
    }

    /**
     * Endpoint para contar notificações não visualizadas
     * Utilizado para exibir contadores e badges na interface
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return Número inteiro de notificações não visualizadas
     */
    @GetMapping("/count-notification")
    public int countNotifications(JwtAuthenticationToken token){
        return this.notificationService.countNotifications(token);
    }
}