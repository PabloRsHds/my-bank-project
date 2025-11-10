package br.com.bank_notification.kafkaConfig;

import br.com.bank_notification.dtos.user.ConsumerDeleteUser;
import br.com.bank_notification.dtos.notification.ConsumerNotificationEvent;
import br.com.bank_notification.dtos.notification.ConsumerNotificationReceivePayment;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

/**
 * Configuração dos consumidores Kafka para o sistema de notificações bancárias
 * Define múltiplos factories para diferentes tipos de mensagens e tópicos
 *
 * @configuration Indica que esta classe é uma configuração Spring
 *
 * @author Pablo R.
 */
@Configuration
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    /**
     * Construtor para injeção de dependências das propriedades Kafka
     *
     * @param kafka Propriedades de configuração do Kafka providas pelo Spring Boot
     */
    @Autowired
    public KafkaConsumerConfig(KafkaProperties kafka) {
        this.kafkaProperties = kafka;
    }

    /**
     * Factory para consumir eventos de notificação genéricos
     * Configura desserializador JSON para mensagens do tipo ConsumerNotificationEvent
     *
     * @return ConsumerFactory configurado para mensagens ConsumerNotificationEvent
     */
    @Bean
    public ConsumerFactory<String, ConsumerNotificationEvent> consumerNotification() {
        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerNotificationEvent> valueDeserializer =
                new JsonDeserializer<>(ConsumerNotificationEvent.class, false);

        valueDeserializer.addTrustedPackages("br.com.bank_notification.dtos.notification");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de eventos de notificação
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerNotificationEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerNotificationEvent> kafkaListenerNotificationFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerNotificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerNotification());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
    //*************************

    /**
     * Factory para consumir eventos de recebimento de pagamento
     * Especializado em processar notificações de transações financeiras
     *
     * @return ConsumerFactory configurado para ConsumerNotificationReceivePayment
     */
    @Bean
    public ConsumerFactory<String, ConsumerNotificationReceivePayment> consumerNotificationReceivePayments() {
        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerNotificationReceivePayment> valueDeserializer =
                new JsonDeserializer<>(ConsumerNotificationReceivePayment.class, false);

        valueDeserializer.addTrustedPackages("br.com.bank_notification.dtos.notification");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de recebimento de pagamento
     *
     * @return ContainerFactory configurado para ConsumerNotificationReceivePayment
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerNotificationReceivePayment> kafkaListenerNotificationReceivePaymentsFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerNotificationReceivePayment> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerNotificationReceivePayments());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    /**
     * Factory para consumir eventos de exclusão de usuário
     * Processa mensagens relacionadas à exclusão de usuários do sistema
     *
     * @return ConsumerFactory configurado para ConsumerDeleteUser
     */
    @Bean
    public ConsumerFactory<String, ConsumerDeleteUser> consumerEventDeleteUser() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerDeleteUser> valueDeserializer =
                new JsonDeserializer<>(ConsumerDeleteUser.class, false);

        valueDeserializer.addTrustedPackages("br.com.bank_notification.dtos.user");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de exclusão de usuário
     *
     * @return ContainerFactory configurado para ConsumerDeleteUser
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> kafkaListenerConsumerDeleteUser() {

        ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerEventDeleteUser());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
    //*********************
}