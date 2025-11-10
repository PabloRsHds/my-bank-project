package br.com.bank_login.kafkaConfig;

import br.com.bank_login.dtos.consumer.ConsumerDeleteUser;
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
 * Configuração do consumidor Kafka para o sistema de login bancário
 * Define factories para consumo de eventos de exclusão de usuários
 *
 * @configuration Indica que esta classe é uma configuração Spring
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
     * Factory para consumir eventos de exclusão de usuário
     * Configura desserializador JSON para mensagens do tipo ConsumerDeleteUser
     *
     * @return ConsumerFactory configurado para mensagens ConsumerDeleteUser
     */
    @Bean
    public ConsumerFactory<String, ConsumerDeleteUser> consumerDeleteUser() {

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerDeleteUser> valueDeserializer =
                new JsonDeserializer<>(ConsumerDeleteUser.class, false);

        valueDeserializer.addTrustedPackages("br.com.bank_login.dtos.consumer");
        valueDeserializer.setUseTypeMapperForKey(false);
        valueDeserializer.setRemoveTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Container factory para listeners de eventos de exclusão de usuário
     * Configura acknowledgment manual para controle explícito de commits
     *
     * @return ContainerFactory configurado para ConsumerDeleteUser
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> kafkaListenerDeleteUserFactory(){
        ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerDeleteUser());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}