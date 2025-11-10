package br.com.bank_notification.consumer;

import br.com.bank_notification.dtos.user.ConsumerDeleteUser;
import br.com.bank_notification.dtos.notification.ConsumerNotificationEvent;
import br.com.bank_notification.dtos.notification.ConsumerNotificationReceivePayment;
import br.com.bank_notification.model.Notification;
import br.com.bank_notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Consumidor Kafka para processamento de eventos de notificação do sistema bancário
 * Responsável por escutar múltiplos tópicos e criar notificações no banco de dados
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    /**
     * Construtor para injeção de dependências do repositório de notificações
     *
     * @param notificationRepository Repositório para operações de banco de dados
     */
    @Autowired
    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Listener para eventos de aprovação de cartão
     * Cria notificação informando que o cartão foi aprovado
     *
     * @param event DTO contendo dados do evento de aprovação
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "notification-card-approved-topic",
            groupId = "notification-approved-group",
            containerFactory = "kafkaListenerNotificationFactory")
    public void createNotificationApproved(ConsumerNotificationEvent event, Acknowledgment ack){

        var notification = new Notification();
        notification.setUserId(event.userId());
        notification.setMessage("Your card was approved");
        this.notificationRepository.save(notification);
        ack.acknowledge();
    }

    /**
     * Listener para eventos de recebimento de pagamento
     * Cria notificação informando sobre valores recebidos
     *
     * @param event DTO contendo dados da transação recebida
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "notification-receive-payment-topic",
            groupId = "notification-receive-payment-group",
            containerFactory = "kafkaListenerNotificationReceivePaymentsFactory")
    public void createNotificationReceivePayment(ConsumerNotificationReceivePayment event, Acknowledgment ack){

        var notification = new Notification();
        notification.setUserId(event.userId());
        notification.setMessage("you received R$"+event.money()+" from "+event.fullName());
        this.notificationRepository.save(notification);
        ack.acknowledge();
    }

    /**
     * Listener para eventos de cancelamento de cartão
     * Cria notificação informando que o cartão foi rejeitado
     *
     * @param event DTO contendo dados do evento de cancelamento
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "notification-card-canceled-topic",
            groupId = "notification-canceled-group",
            containerFactory = "kafkaListenerNotificationFactory")
    public void createNotificationRejected(ConsumerNotificationEvent event, Acknowledgment ack){

        var notification = new Notification();
        notification.setUserId(event.userId());
        notification.setMessage("Your card was rejected");
        this.notificationRepository.save(notification);
        ack.acknowledge();
    }

    /**
     * Listener para eventos de boas-vindas
     * Cria notificação de welcome para novos usuários
     *
     * @param event DTO contendo dados do usuário novo
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "welcome-topic",
            groupId = "welcome-group",
            containerFactory = "kafkaListenerNotificationFactory")
    public void consumerEventWelcomeUser(ConsumerNotificationEvent event, Acknowledgment ack){

        var notification = new Notification();
        notification.setUserId(event.userId());
        notification.setMessage("Welcome to the My-Bank website!");
        this.notificationRepository.save(notification);
        ack.acknowledge();
    }

    /**
     * Listener para eventos de exclusão de usuário
     * Remove todas as notificações associadas ao usuário excluído
     *
     * @param consumer DTO contendo ID do usuário a ser excluído
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "delete-user-topic",
            groupId = "delete-user-group",
            containerFactory = "kafkaListenerConsumerDeleteUser")
    public void deleteUserId(ConsumerDeleteUser consumer, Acknowledgment ack){

        this.notificationRepository.deleteAllByUserId(consumer.userId());
        ack.acknowledge();
    }

    /**
     * Listener para eventos de aprovação de limite de cartão
     * Cria notificação informando que o limite foi aprovado
     *
     * @param event DTO contendo dados do evento de aprovação de limite
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "notification-limit-card-approved-topic",
            groupId = "notification-limit-card-approved-group",
            containerFactory = "kafkaListenerNotificationFactory")
    public void createNotificationLimitCreditApproved(ConsumerNotificationEvent event, Acknowledgment ack){

        var notification = new Notification();
        notification.setUserId(event.userId());
        notification.setMessage("Your credit limit was approved");
        this.notificationRepository.save(notification);
        ack.acknowledge();
    }

    /**
     * Listener para eventos de rejeição de limite de cartão
     * Cria notificação informando que o limite foi rejeitado
     *
     * @param event DTO contendo dados do evento de rejeição de limite
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "notification-limit-card-rejected-topic",
            groupId = "notification-limit-card-rejected-group",
            containerFactory = "kafkaListenerNotificationFactory")
    public void createNotificationLimitCreditRejected(ConsumerNotificationEvent event, Acknowledgment ack){

        var notification = new Notification();
        notification.setUserId(event.userId());
        notification.setMessage("Your credit limit was rejected");
        this.notificationRepository.save(notification);
        ack.acknowledge();
    }
}