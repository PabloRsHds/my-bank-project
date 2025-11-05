package br.com.email.kafkaConfig;

import br.com.email.dto.EmailVerificationConsumer;
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
 * Configuração do consumidor Kafka para o microsserviço de e-mail
 * Define as fábricas de consumidores para processamento de mensagens de verificação de e-mail
 *
 * @configuration Indica que esta classe contém definições de beans Spring
 * @author Pablo R.
 */
@Configuration
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    /**
     * Construtor para injeção das propriedades do Kafka
     * @param kafka Propriedades configuradas no application.yml/properties
     */
    @Autowired
    public KafkaConsumerConfig(KafkaProperties kafka) {
        this.kafkaProperties = kafka;
    }

    /**
     * Fábrica de consumidor para mensagens de verificação de e-mail
     * Responsável por desserializar mensagens do tópico de verificação de e-mail
     *
     * @return ConsumerFactory configurado para mensagens EmailVerificationConsumer
     * @bean Define um bean gerenciado pelo Spring
     * @security Configurado com pacotes confiáveis para prevenção de ataques de desserialização
     */
    @Bean
    public ConsumerFactory<String, EmailVerificationConsumer> consumerEmailVerification(){

        Map<String, Object> props = this.kafkaProperties.buildConsumerProperties();

        JsonDeserializer<EmailVerificationConsumer> valueDeserializer = new JsonDeserializer<>(
                EmailVerificationConsumer.class, false);

        valueDeserializer.addTrustedPackages("br.com.email.dto");
        valueDeserializer.setUseTypeMapperForKey(false);
        valueDeserializer.setRemoveTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Factory do listener para mensagens de verificação de e-mail
     * Configura o container que gerencia os consumidores de e-mail
     *
     * @return ContainerFactory para EmailVerificationConsumer
     * @ackMode MANUAL Requer confirmação explícita após o envio bem-sucedido do e-mail
     * @concurrent Suporta consumo paralelo de múltiplas mensagens
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailVerificationConsumer> kafkaListenerEmailVerificationFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailVerificationConsumer> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerEmailVerification());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}