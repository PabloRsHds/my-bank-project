package br.com.bank_wallet.consumer;

import br.com.bank_wallet.dtos.payment.ConsumerSendPaymentEvent;
import br.com.bank_wallet.dtos.payment.EventNotificationPayment;
import br.com.bank_wallet.enums.SendOrReceive;
import br.com.bank_wallet.feign.UserClient;
import br.com.bank_wallet.models.Payment;
import br.com.bank_wallet.repositories.PaymentRepository;
import br.com.bank_wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Consumidor Kafka para processamento de eventos de recebimento de pagamentos
 * Responsável por processar transações recebidas e atualizar saldos das carteiras
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class PaymentConsumer {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final UserClient userClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Construtor para injeção de dependências do consumidor de pagamentos
     *
     * @param paymentRepository Repositório para operações de banco de dados de pagamentos
     * @param walletRepository Repositório para operações de banco de dados de carteiras
     * @param userClient Cliente Feign para comunicação com microserviço de usuários
     * @param kafkaTemplate Template para comunicação assíncrona via Kafka
     */
    @Autowired
    public PaymentConsumer(PaymentRepository paymentRepository,
                           WalletRepository walletRepository,
                           UserClient userClient,
                           KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
        this.userClient = userClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Listener para eventos de recebimento de pagamento
     * Processa transações recebidas e atualiza o saldo da carteira do destinatário
     *
     * @param event DTO contendo dados da transação recebida
     * @param ack Objeto para confirmação manual do offset Kafka
     *
     * @implSpec Fluxo de processamento:
     * 1. Cria registro de pagamento recebido
     * 2. Atualiza saldo da carteira do destinatário
     * 3. Envia notificação via Kafka para o serviço de notificações
     * 4. Confirma o offset do Kafka
     */
    @KafkaListener(topics = "receive-payment-topic",
            groupId = "receive-payment-groupId",
            containerFactory = "kafkaListenersSendPaymentConsumer")
    private void consumerSendPayment(ConsumerSendPaymentEvent event, Acknowledgment ack){

        System.out.println("Kafka recebeu evento: " + event);
        var receivedPayment = new Payment();
        receivedPayment.setUserSend(event.userSend());
        receivedPayment.setUserReceive(event.userReceive());
        receivedPayment.setMoney(event.money());
        receivedPayment.setSendOrReceive(SendOrReceive.RECEIVE);
        receivedPayment.setPixOrCredit(event.pixOrCredit());
        this.paymentRepository.save(receivedPayment);

        var wallet = this.walletRepository.findByUserId(event.userReceive());
        wallet.get().setMoney(wallet.get().getMoney() + event.money());
        this.walletRepository.save(wallet.get());

        var user = this.userClient.findByUserWithCpfOrPhoneOrEmail(event.userReceive());
        this.kafkaTemplate.send("notification-receive-payment-topic",
                new EventNotificationPayment(event.userReceive(), user.fullName(), event.money()));

        ack.acknowledge();
    }
}