package br.com.bank_wallet.service;

import br.com.bank_wallet.dtos.payment.*;
import br.com.bank_wallet.enums.PixOrCredit;
import br.com.bank_wallet.enums.SendOrReceive;
import br.com.bank_wallet.feign.CardClient;
import br.com.bank_wallet.feign.UserClient;
import br.com.bank_wallet.models.Payment;
import br.com.bank_wallet.repositories.PaymentRepository;
import br.com.bank_wallet.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Serviço principal para gerenciamento de pagamentos e transações financeiras
 * Responsável por operações de envio, recebimento e consulta de transações
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final UserClient userClient;
    private final CardClient cardClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Construtor para injeção de dependências do serviço de pagamentos
     *
     * @param repository1 Repositório para operações de banco de dados de pagamentos
     * @param repository2 Repositório para operações de banco de dados de carteiras
     * @param client Cliente Feign para comunicação com microserviço de usuários
     * @param cardClient Cliente Feign para comunicação com microserviço de cartões
     * @param kafkaTemplate Template para comunicação assíncrona via Kafka
     */
    public PaymentService(PaymentRepository repository1,
                          WalletRepository repository2,
                          UserClient client,
                          CardClient cardClient,
                          KafkaTemplate<String, Object> kafkaTemplate) {
        this.paymentRepository = repository1;
        this.walletRepository = repository2;
        this.userClient = client;
        this.cardClient = cardClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Processa um pagamento entre usuários do sistema
     * Suporta transações via PIX e Cartão de Crédito com validações de saldo
     *
     * @param token Token JWT de autenticação contendo ID do usuário remetente
     * @param request DTO com dados do pagamento (valor, chave destino, método)
     * @return ResponseEntity com resultado da operação
     *
     * @implSpec Fluxo de pagamento:
     * 1. Valida existência e saldo da carteira do remetente
     * 2. Identifica destinatário por CPF, telefone ou email
     * 3. Processa pagamento via crédito ou PIX
     * 4. Registra transação e notifica destinatário via Kafka
     */
    @Transactional
    public ResponseEntity<Map<String, String>> payment(
            JwtAuthenticationToken token, RequestPayment request) {

        // 1. Verifica se o usuario existe
        var senderWallet = walletRepository.findByUserId(token.getName());

        if (senderWallet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (request.key().isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var user = this.userClient.findByUserWithCpfOrPhoneOrEmail(request.key());

        var receiveWallet = walletRepository.findByUserId(user.userId());

        if (receiveWallet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 4. Cria e salva o envio de  pagamento
        if (request.pixOrCredit().equals(PixOrCredit.CREDIT)) {
            var sendPayment = new Payment();
            sendPayment.setUserSend(token.getName());
            sendPayment.setUserReceive(user.userId());
            sendPayment.setMoney(request.money());
            sendPayment.setSendOrReceive(SendOrReceive.SEND);
            sendPayment.setPixOrCredit(request.pixOrCredit());
            paymentRepository.save(sendPayment);

            //Depois adicionar um feign card para verificar se há o valor para enviar com o crédito!
            var response = this.cardClient.paymentWithCredit(token.getName(), request.money());

            if (response.equals("INSUFICIENTE")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "Bad request","insufficient money"
                ));
            }

            this.kafkaTemplate.send("receive-payment-topic", new
                    EventSendPayment(token.getName(), user.userId(), request.money(), request.pixOrCredit()));

            return ResponseEntity.ok().build();
        }

        // 2. Verifica saldo
        if (senderWallet.get().getMoney() < request.money()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("bad_request", "You don't have that money"));
        }

        var sendPayment = new Payment();
        sendPayment.setUserSend(token.getName());
        sendPayment.setUserReceive(user.userId());
        sendPayment.setMoney(request.money());
        sendPayment.setSendOrReceive(SendOrReceive.SEND);
        sendPayment.setPixOrCredit(request.pixOrCredit());
        paymentRepository.save(sendPayment);

        senderWallet.get().setMoney(senderWallet.get().getMoney() - request.money());
        this.walletRepository.save(senderWallet.get());

        this.kafkaTemplate.send("receive-payment-topic", new
                EventSendPayment(token.getName(), user.userId(), request.money(), request.pixOrCredit()));

        return ResponseEntity.ok().build();
    }

    /**
     * Recupera todos os pagamentos enviados pelo usuário autenticado
     * Retorna lista ordenada por data decrescente (mais recentes primeiro)
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de pagamentos enviados
     */
    public ResponseEntity<List<ResponsePayments>> getAllSendPaymentsForUser(JwtAuthenticationToken token) {

        var payments = this.paymentRepository.findAllByUserSend(token.getName())
                .stream()
                .filter(payment -> payment.getSendOrReceive().equals(SendOrReceive.SEND)) // Filtro aqui
                .sorted(Comparator.comparing(Payment::getTimeStamp).reversed()) // Ordenação por data decrescente
                .map(payment ->
                        new ResponsePayments(payment.getUserSend(), payment.getUserReceive(),
                                payment.getMoney(), payment.getSendOrReceive(),
                                payment.getTimeStamp()))
                .toList();

        return ResponseEntity.ok(payments);
    }

    /**
     * Recupera todos os pagamentos recebidos pelo usuário autenticado
     * Retorna lista ordenada por data decrescente (mais recentes primeiro)
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista de pagamentos recebidos
     */
    public ResponseEntity<List<ResponsePayments>> getAllReceivePaymentsForUser(JwtAuthenticationToken token) {

        var payments = this.paymentRepository.findAllByUserReceive(token.getName())
                .stream()
                .filter(payment -> payment.getSendOrReceive().equals(SendOrReceive.RECEIVE))
                .sorted(Comparator.comparing(Payment::getTimeStamp).reversed())
                .map(payment ->
                        new ResponsePayments(payment.getUserSend(), payment.getUserReceive(),
                                payment.getMoney(), payment.getSendOrReceive(),
                                payment.getTimeStamp()))
                .toList();

        return ResponseEntity.ok(payments);
    }

    /**
     * Processa pagamento para quitação de débito em cartão de crédito
     * Utiliza saldo da carteira PIX para pagar fatura do cartão
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @param request DTO com valor do pagamento para quitação
     * @return ResponseEntity com resultado da operação
     */
    @Transactional
    public ResponseEntity<Map<String, String>> creditPayment(
            JwtAuthenticationToken token, RequestCreditPayment request) {

        var wallet = this.walletRepository.findByUserId(token.getName());

        if (wallet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 2. Verifica saldo
        if (wallet.get().getMoney() < request.money()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("bad_request", "You don't have that money"));
        }

        var sendPayment = new Payment();
        sendPayment.setUserSend(token.getName());
        sendPayment.setUserReceive(null);
        sendPayment.setMoney(request.money());
        sendPayment.setSendOrReceive(SendOrReceive.SEND);
        sendPayment.setPixOrCredit(PixOrCredit.PIX);
        paymentRepository.save(sendPayment);

        wallet.get().setMoney(wallet.get().getMoney() - request.money());
        this.walletRepository.save(wallet.get());

        this.kafkaTemplate.send("payment-limit-card-topic",
                new EventCreditPayment(token.getName(), request.money()));

        return ResponseEntity.ok().build();
    }
}