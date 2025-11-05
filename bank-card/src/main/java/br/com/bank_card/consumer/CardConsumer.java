package br.com.bank_card.consumer;

import br.com.bank_card.dtos.*;
import br.com.bank_card.enums.Status;
import br.com.bank_card.enums.TypeCard;
import br.com.bank_card.model.Card;
import br.com.bank_card.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

/**
 * Consumidor Kafka para processamento de eventos relacionados a cartões bancários
 * Responsável por criar, aprovar, rejeitar e gerenciar cartões baseado em eventos do sistema
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class CardConsumer {

    private final CardRepository cardRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Construtor para injeção de dependências do consumidor de cartões
     *
     * @param cardRepository Repositório para operações de banco de dados de cartões
     * @param kafkaTemplate Template para comunicação assíncrona via Kafka
     */
    @Autowired
    public CardConsumer(
            CardRepository cardRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.cardRepository = cardRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Listener para eventos de aprovação de cartão
     * Cria novo cartão ou reativa cartão existente com status aprovado
     *
     * @param event DTO contendo dados do usuário para criação do cartão
     * @param ack Objeto para confirmação manual do offset Kafka
     *
     * @implSpec Fluxo de criação:
     * 1. Gera número do cartão, data de expiração e CVV aleatórios
     * 2. Define expiração para 7 anos no futuro
     * 3. Cartão criado como tipo DÉBITO inicialmente
     * 4. Envia notificação de aprovação via Kafka
     */
    @KafkaListener(topics = "approved-card-topic",
            groupId = "create-card-group",
            containerFactory = "kafkaListenerCard")
    public void createCard(ConsumerCardEvent event, Acknowledgment ack){

        // Verifica se já existe cartão desse tipo para esse usuário
        var cardAlreadyExists = cardRepository
                .findByUserId(event.userId());

        if (cardAlreadyExists.isEmpty()) {
            // Geração do novo cartão
            Card newCard = new Card();
            newCard.setUserId(event.userId());
            newCard.setFullName(event.fullName());
            newCard.setRg(event.rg());
            newCard.setCpf(event.cpf());
            newCard.setCardNumber(String.format("%04d %04d %04d %04d",
                    new Random().nextInt(10000),
                    new Random().nextInt(10000),
                    new Random().nextInt(10000),
                    new Random().nextInt(10000)));
            newCard.setExpirationDate(YearMonth.now().plusYears(7).format(DateTimeFormatter.ofPattern("MM/yy")));
            newCard.setCardCvv(String.format("%03d", new Random().nextInt(1000)));
            newCard.setStatus(Status.APPROVED);
            newCard.setTypeOfCard(TypeCard.DEBIT);

            cardRepository.save(newCard);

            this.kafkaTemplate.send("notification-card-approved-topic",
                    new NotificationEvent(event.userId()));

            ack.acknowledge();
        }

        if (cardAlreadyExists.get().getStatus().equals(Status.APPROVED)) {
            return;
        }

        else if (cardAlreadyExists.get().getStatus().equals(Status.BLOCKED) ||
                cardAlreadyExists.get().getStatus().equals(Status.CANCELED)) {

            cardAlreadyExists.get().setStatus(Status.APPROVED);
            cardRepository.save(cardAlreadyExists.get());

            this.kafkaTemplate.send("notification-card-approved-topic",
                    new NotificationEvent(event.userId()));

            ack.acknowledge();
        }
    }

    /**
     * Listener para eventos de aprovação de limite de crédito
     * Adiciona limite baseado em 30% do salário e atualiza cartão para tipo MÚLTIPLO
     *
     * @param event DTO contendo ID do usuário e salário para cálculo do limite
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "approved-limit-card-topic",
            groupId = "approved-limit-card-group",
            containerFactory = "kafkaListenerCreditLimitApproval")
    public void creditLimitApproval(ConsumerCreditLimitApproval event, Acknowledgment ack){

        // Verifica se já existe cartão desse tipo para esse usuário
        var cardAlreadyExists = cardRepository
                .findByUserId(event.userId());


        if (cardAlreadyExists.isPresent()) {

            var salary = (0.3 * event.salary());

            cardAlreadyExists.get().setLimitCredit(salary);
            cardAlreadyExists.get().setTypeOfCard(TypeCard.MULTIPLE);
            this.cardRepository.save(cardAlreadyExists.get());

            this.kafkaTemplate.send("notification-limit-card-approved-topic",
                    new NotificationEvent(event.userId()));

            ack.acknowledge();
        }
    }

    /**
     * Listener para eventos de rejeição de limite de crédito
     * Remove limite de crédito e redefine cartão para tipo DÉBITO
     *
     * @param event DTO contendo ID do usuário para atualização do cartão
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "rejected-limit-card-topic",
            groupId = "rejected-limit-card-group",
            containerFactory = "kafkaListenerCreditLimitRejected")
    public void creditLimitRejected(ConsumerCreditLimitRejected event, Acknowledgment ack){

        // Verifica se já existe cartão desse tipo para esse usuário
        var cardAlreadyExists = cardRepository
                .findByUserId(event.userId());


        if (cardAlreadyExists.isPresent()) {

            cardAlreadyExists.get().setLimitCredit(null);
            cardAlreadyExists.get().setTypeOfCard(TypeCard.DEBIT);
            this.cardRepository.save(cardAlreadyExists.get());

            this.kafkaTemplate.send("notification-limit-card-rejected-topic",
                    new NotificationEvent(event.userId()));

            ack.acknowledge();
        }
    }

    /**
     * Listener para eventos de cancelamento de cartão
     * Altera status do cartão para CANCELED se estiver atualmente APROVADO
     *
     * @param event DTO contendo ID do usuário para cancelamento do cartão
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "canceled-card-topic",
            groupId = "canceled-card-group",
            containerFactory = "kafkaListenerCard")
    public void rejectCard(ConsumerCardEvent event, Acknowledgment ack){

        // Verifica se não existe cartão desse tipo para esse usuário
        var cardAlreadyExists = cardRepository
                .findByUserId(event.userId());

        if (cardAlreadyExists.isEmpty()) {
            return;
        }

        else if (cardAlreadyExists.get().getStatus().equals(Status.APPROVED)) {

            cardAlreadyExists.get().setStatus(Status.CANCELED);
            cardRepository.save(cardAlreadyExists.get());

            this.kafkaTemplate.send("notification-card-canceled-topic",
                    new NotificationEvent(event.userId()));

            ack.acknowledge();
        }

    }

    /**
     * Listener para eventos de exclusão de usuário
     * Remove todos os cartões associados ao usuário excluído
     *
     * @param consumer DTO contendo ID do usuário para exclusão dos cartões
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "delete-user-topic",
            groupId = "delete-user-group3",
            containerFactory = "kafkaListenerConsumerDeleteUser")
    public void deleteUserId(ConsumerDeleteUser consumer, Acknowledgment ack){

        this.cardRepository.deleteAllByUserId(consumer.userId());
        ack.acknowledge();
    }

    /**
     * Listener para eventos de pagamento de limite de cartão
     * Adiciona valor ao limite de crédito do cartão do usuário
     *
     * @param consumer DTO contendo ID do usuário e valor a ser adicionado ao limite
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "payment-limit-card-topic",
            groupId = "payment-limit-card-group",
            containerFactory = "kafkaListenerCreditPayment")
    public void paymentCard(ConsumerCreditPayment consumer, Acknowledgment ack){

        Optional<Card> card = this.cardRepository.findByUserId(consumer.userId());

        card.get().setLimitCredit(card.get().getLimitCredit() + consumer.money());
        this.cardRepository.save(card.get());
        ack.acknowledge();
    }
}