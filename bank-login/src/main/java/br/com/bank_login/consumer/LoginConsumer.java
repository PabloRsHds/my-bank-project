package br.com.bank_login.consumer;

import br.com.bank_login.dtos.ConsumerDeleteUser;
import br.com.bank_login.repository.LoginRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Consumidor Kafka para processamento de eventos de exclusão de usuários
 * Responsável por limpar registros de login quando usuários são excluídos do sistema
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class LoginConsumer {

    private final LoginRepository loginRepository;

    /**
     * Construtor para injeção de dependências do repositório de logins
     *
     * @param repository Repositório para operações de banco de dados
     */
    public LoginConsumer(LoginRepository repository) {
        this.loginRepository = repository;
    }

    /**
     * Listener para eventos de exclusão de usuário
     * Remove todos os registros de login associados ao usuário excluído
     *
     * @param consumer DTO contendo ID do usuário a ser excluído
     * @param ack Objeto para confirmação manual do offset Kafka
     */
    @KafkaListener(topics = "delete-user-topic",
            groupId = "delete-user-group1",
            containerFactory = "kafkaListenerDeleteUserFactory")
    @Transactional
    public void deleteUserId(ConsumerDeleteUser consumer, Acknowledgment ack){

        this.loginRepository.deleteAllByUserId(consumer.userId());
        ack.acknowledge();
    }
}