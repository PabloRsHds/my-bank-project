package br.com.bank_wallet.kafkaConfig;

import br.com.bank_wallet.dtos.ConsumerSendPaymentEvent;
import br.com.bank_wallet.dtos.ConsumerWalletEvent;
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
 * Configuração dos consumidores Kafka para o sistema de carteira bancária
 * Define factories para consumo de eventos de carteira e pagamentos
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
     * Factory para consumir eventos de carteira
     * Configura desserializador JSON para mensagens do tipo ConsumerWalletEvent
     *
     * @return ConsumerFactory configurado para mensagens ConsumerWalletEvent
     */
    @Bean
    public ConsumerFactory<String, ConsumerWalletEvent> consumerWallet() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerWalletEvent> valueDeserializer =
                new JsonDeserializer<>(ConsumerWalletEvent.class, false);

        valueDeserializer.addTrustedPackages("br.com.picpay_wallet.dtos");
        valueDeserializer.setUseTypeMapperForKey(false);
        valueDeserializer.setRemoveTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de eventos de carteira
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerWalletEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerWalletEvent> kafkaListenersWalletConsumer() {

        ConcurrentKafkaListenerContainerFactory<String, ConsumerWalletEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerWallet());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    /**
     * Factory para consumir eventos de envio de pagamento
     * Configura desserializador JSON para mensagens do tipo ConsumerSendPaymentEvent
     *
     * @return ConsumerFactory configurado para mensagens ConsumerSendPaymentEvent
     */
    @Bean
    public ConsumerFactory<String, ConsumerSendPaymentEvent> consumerSendPayment() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerSendPaymentEvent> valueDeserializer =
                new JsonDeserializer<>(ConsumerSendPaymentEvent.class, false);

        valueDeserializer.addTrustedPackages("br.com.picpay_wallet.dtos");
        valueDeserializer.setUseTypeMapperForKey(false);
        valueDeserializer.setRemoveTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de eventos de pagamento
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerSendPaymentEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerSendPaymentEvent> kafkaListenersSendPaymentConsumer() {

        ConcurrentKafkaListenerContainerFactory<String, ConsumerSendPaymentEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerSendPayment());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}