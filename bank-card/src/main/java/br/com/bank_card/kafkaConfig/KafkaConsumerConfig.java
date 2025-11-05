package br.com.bank_card.kafkaConfig;

import br.com.bank_card.dtos.*;
import org.apache.kafka.common.serialization.StringDeserializer;
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
 * Configuração dos consumidores Kafka para o sistema de cartões bancários
 * Define múltiplos factories para consumo de eventos relacionados a cartões e limites de crédito
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
    public KafkaConsumerConfig(KafkaProperties kafka) {
        this.kafkaProperties = kafka;
    }

    /**
     * Factory para consumir eventos de criação de cartão
     * Configura desserializador JSON para mensagens do tipo ConsumerCardEvent
     *
     * @return ConsumerFactory configurado para mensagens ConsumerCardEvent
     */
    @Bean
    public ConsumerFactory<String, ConsumerCardEvent> consumerCardFactory() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerCardEvent> valueDeserializer =
                new JsonDeserializer<>(ConsumerCardEvent.class, false);

        valueDeserializer.addTrustedPackages("br.com.picpay_card.dtos");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de eventos de cartão
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerCardEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerCardEvent> kafkaListenerCard() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerCardEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerCardFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    //*********************************

    /**
     * Factory para consumir eventos de aprovação de limite de crédito
     * Configura desserializador JSON para mensagens do tipo ConsumerCreditLimitApproval
     *
     * @return ConsumerFactory configurado para mensagens ConsumerCreditLimitApproval
     */
    @Bean
    public ConsumerFactory<String, ConsumerCreditLimitApproval> consumerCreditLimitApproval() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerCreditLimitApproval> valueDeserializer =
                new JsonDeserializer<>(ConsumerCreditLimitApproval.class, false);

        valueDeserializer.addTrustedPackages("br.com.picpay_card.dtos");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de aprovação de limite de crédito
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerCreditLimitApproval
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditLimitApproval> kafkaListenerCreditLimitApproval() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditLimitApproval> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerCreditLimitApproval());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    /**
     * Factory para consumir eventos de rejeição de limite de crédito
     * Configura desserializador JSON para mensagens do tipo ConsumerCreditLimitRejected
     *
     * @return ConsumerFactory configurado para mensagens ConsumerCreditLimitRejected
     */
    @Bean
    public ConsumerFactory<String, ConsumerCreditLimitRejected> consumerCreditLimitRejected() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerCreditLimitRejected> valueDeserializer =
                new JsonDeserializer<>(ConsumerCreditLimitRejected.class, false);

        valueDeserializer.addTrustedPackages("br.com.picpay_card.dtos");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de rejeição de limite de crédito
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerCreditLimitRejected
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditLimitRejected> kafkaListenerCreditLimitRejected() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditLimitRejected> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerCreditLimitRejected());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
    //******************

    /**
     * Factory para consumir eventos de pagamento de crédito
     * Configura desserializador JSON para mensagens do tipo ConsumerCreditPayment
     *
     * @return ConsumerFactory configurado para mensagens ConsumerCreditPayment
     */
    @Bean
    public ConsumerFactory<String, ConsumerCreditPayment> consumerCreditPayment() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerCreditPayment> valueDeserializer =
                new JsonDeserializer<>(ConsumerCreditPayment.class, false);

        valueDeserializer.addTrustedPackages("br.com.picpay_card.dtos");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de pagamento de crédito
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerCreditPayment
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditPayment> kafkaListenerCreditPayment() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditPayment> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerCreditPayment());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    /**
     * Factory para consumir eventos de exclusão de usuário
     * Configura desserializador JSON para mensagens do tipo ConsumerDeleteUser
     *
     * @return ConsumerFactory configurado para mensagens ConsumerDeleteUser
     */
    @Bean
    public ConsumerFactory<String, ConsumerDeleteUser> consumerDeleteUser() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerDeleteUser> valueDeserializer =
                new JsonDeserializer<>(ConsumerDeleteUser.class, false);

        valueDeserializer.addTrustedPackages("br.com.picpay_card.dtos");
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
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerDeleteUser
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> kafkaListenerConsumerDeleteUser() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerDeleteUser());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    /**
     * Factory para consumir mensagens de texto simples
     * Utilizado para mensagens Kafka com payload simples do tipo String
     *
     * @return ConsumerFactory configurado para mensagens String
     */
    @Bean
    public ConsumerFactory<String, String> consumerEventString() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    /**
     * Container factory para listeners de mensagens de texto
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para mensagens String
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerConsumerEventString() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerEventString());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}