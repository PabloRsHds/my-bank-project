package br.com.email.consumer;

import br.com.email.dto.EmailVerificationConsumer;
import br.com.email.service.EmailService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Serviço consumidor de mensagens Kafka para processamento de solicitações de e-mail
 * Responsável por ouvir eventos de verificação de e-mail e orquestrar o envio
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 * @author Pablo R.
 */
@Service
public class ConsumerService {

    private static final Logger log = LoggerFactory.getLogger(ConsumerService.class);
    private final EmailService emailService;

    /**
     * Construtor para injeção de dependência do EmailService
     * @param service Serviço de e-mail responsável pelo envio efetivo dos e-mails
     */
    public ConsumerService(EmailService service){
        this.emailService = service;
    }

    /**
     * Listener para mensagens de verificação de e-mail
     * Processa solicitações de envio de e-mail de verificação e confirma o processamento
     *
     * @param consumer DTO contendo e-mail do destinatário e código de verificação
     * @param ack Objeto para confirmação manual do offset da mensagem
     * @throws MessagingException Em caso de erro durante o envio do e-mail
     * @throws RuntimeException Em caso de erro inesperado durante o processamento
     *
     * @kafkaListener Configurado para o tópico "email-verification-topic" com groupId "email-service-group"
     * @ackMode MANUAL Requer confirmação explícita após o envio bem-sucedido do e-mail
     *
     * @implNote Fluxo de processamento:
     * 1. Log da solicitação recebida
     * 2. Delegação do envio para EmailService
     * 3. Confirmação da mensagem se bem-sucedida
     * 4. Exceção em caso de falha (mensagem não confirmada para reprocessamento)
     */
    @KafkaListener(topics = "email-verification-topic",
            groupId = "email-service-group",
            containerFactory = "kafkaListenerEmailVerificationFactory")
    public void listen(EmailVerificationConsumer consumer, Acknowledgment ack) throws MessagingException {
        try {
            log.info("Verification request received for: {}", consumer.email());
            this.emailService.sendEmail(consumer);
            ack.acknowledge();
        } catch (Exception e){
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}