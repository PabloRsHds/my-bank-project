package br.com.bank_document.kafkaConfig;

import br.com.bank_document.dtos.document.ConsumerCardAnalysis;
import br.com.bank_document.dtos.creditDocument.ConsumerCreditDocuments;
import br.com.bank_document.dtos.user.ConsumerDeleteUser;
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
 * Configuração dos consumidores Kafka para o microsserviço de documentos
 * Define as fábricas de consumidores para diferentes tipos de mensagens
 *
 * @configuration Indica que esta classe contém definições de beans Spring
 *
 * @author Pablo R.
 */
@Configuration
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    /**
     * Construtor para injeção das propriedades do Kafka
     * @param kafkaProperties Propriedades configuradas no application.yml/properties
     */
    public KafkaConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    // ------------------------
    // 1. ConsumerCardAnalysis
    // ------------------------

    /**
     * Fábrica de consumidor para mensagens de análise de cartão
     * Responsável por desserializar mensagens do tópico de documentos para cartão
     *
     * @return ConsumerFactory configurado para mensagens ConsumerCardAnalysis
     * @bean Define um bean gerenciado pelo Spring
     */
    @Bean
    public ConsumerFactory<String, ConsumerCardAnalysis> consumerCardAnalysisFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerCardAnalysis> valueDeserializer =
                new JsonDeserializer<>(ConsumerCardAnalysis.class, false);
        valueDeserializer.addTrustedPackages("br.com.bank_document.dtos.document");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Factory do listener para mensagens de análise de cartão
     * Configura o container que gerencia os consumidores
     *
     * @return ContainerFactory para ConsumerCardAnalysis
     * @ackMode MANUAL Requer confirmação explícita das mensagens
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerCardAnalysis> kafkaListenerCardAnalysisFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerCardAnalysis> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerCardAnalysisFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ------------------------
    // 2. ConsumerCreditDocuments
    // ------------------------

    /**
     * Fábrica de consumidor para mensagens de análise de crédito
     * Responsável por desserializar mensagens do tópico de documentos de crédito
     *
     * @return ConsumerFactory configurado para mensagens ConsumerCreditDocuments
     */
    @Bean
    public ConsumerFactory<String, ConsumerCreditDocuments> consumerCreditDocumentsFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerCreditDocuments> valueDeserializer =
                new JsonDeserializer<>(ConsumerCreditDocuments.class, false);
        valueDeserializer.addTrustedPackages("br.com.bank_document.dtos.creditDocument");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Factory do listener para mensagens de análise de crédito
     * Configura o container que gerencia os consumidores de crédito
     *
     * @return ContainerFactory para ConsumerCreditDocuments
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditDocuments> kafkaListenerCreditDocumentsFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerCreditDocuments> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerCreditDocumentsFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // ------------------------
    // 3. DeleteUserDTO
    // ------------------------

    /**
     * Fábrica de consumidor para mensagens de exclusão de usuário
     * Responsável por desserializar mensagens do tópico de exclusão de usuários
     *
     * @return ConsumerFactory configurado para mensagens ConsumerDeleteUser
     */
    @Bean
    public ConsumerFactory<String, ConsumerDeleteUser> consumerDeleteUserFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        JsonDeserializer<ConsumerDeleteUser> valueDeserializer =
                new JsonDeserializer<>(ConsumerDeleteUser.class, false);

        valueDeserializer.addTrustedPackages("br.com.bank_document.dtos.user");
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    /**
     * Factory do listener para mensagens de exclusão de usuário
     * Configura o container que gerencia os consumidores de exclusão
     *
     * @return ContainerFactory para ConsumerDeleteUser
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> kafkaListenerDeleteUserFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsumerDeleteUser> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerDeleteUserFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}