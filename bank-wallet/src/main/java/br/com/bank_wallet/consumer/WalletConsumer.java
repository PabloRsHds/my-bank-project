package br.com.bank_wallet.consumer;

import br.com.bank_wallet.dtos.wallet.ConsumerWalletEvent;
import br.com.bank_wallet.models.Wallet;
import br.com.bank_wallet.repositories.WalletRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Consumidor Kafka para processamento de eventos de criação de carteiras
 * Responsável por criar carteiras digitais para novos usuários do sistema
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class WalletConsumer {

    private final WalletRepository walletRepository;

    /**
     * Construtor para injeção de dependências do repositório de carteiras
     *
     * @param repository Repositório para operações de banco de dados de carteiras
     */
    public WalletConsumer(WalletRepository repository) {
        this.walletRepository = repository;
    }

    /**
     * Listener para eventos de criação de carteira
     * Cria uma nova carteira digital com saldo inicial para usuários recém-registrados
     *
     * @param event DTO contendo ID do usuário para criação da carteira
     * @param ack Objeto para confirmação manual do offset Kafka
     *
     * @implNote Cada nova carteira é criada com um saldo inicial de R$ 50,00
     * como benefício de boas-vindas ao sistema bancário
     */
    @KafkaListener(topics = "creation-wallet-topic",
            groupId = "creation-wallet-groupId",
            containerFactory = "kafkaListenersWalletConsumer")
    private void createWallet(ConsumerWalletEvent event, Acknowledgment ack){

        var wallet = new Wallet();
        wallet.setUserId(event.userId());
        wallet.setMoney(50.00);
        this.walletRepository.save(wallet);
        ack.acknowledge();
    }
}