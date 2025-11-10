package br.com.bank_document.services.creditService;

import br.com.bank_document.dtos.creditDocument.*;
import br.com.bank_document.enums.Status;
import br.com.bank_document.microservice.UserClient;
import br.com.bank_document.models.CreditDocument;
import br.com.bank_document.repositories.CreditDocumentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de documentos de crédito no sistema bancário
 * Responsável por operações de análise de crédito, aprovação de limite e gestão de documentos financeiros
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 * @author Pablo R.
 */
@Slf4j
@Service
public class CreditDocumentService {

    private final CreditDocumentRepository creditDocumentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserClient userClient;

    /**
     * Construtor para injeção de dependências
     * @param repository Repositório para operações de banco de dados de crédito
     * @param kafka Template para comunicação assíncrona via Kafka
     */
    @Autowired
    public CreditDocumentService(CreditDocumentRepository repository,
                                 KafkaTemplate<String, Object> kafka,
                                 UserClient userClient){
        this.creditDocumentRepository = repository;
        this.kafkaTemplate = kafka;
        this.userClient = userClient;
    }


    /**
     * Processa documentos para análise de crédito
     * Valida e armazena comprovante de renda e dados profissionais
     *
     * @param token Token JWT de autenticação
     * @param request DTO com documentos e informações profissionais
     * @return ResponseEntity com confirmação do envio para análise
     * @throws IOException Em caso de erro no armazenamento dos arquivos
     * @apiNote Envia dados para análise de limite de crédito via Kafka
     */
    public ResponseEntity<Map<String, String>> creditDocumentsForAnalysis(
            JwtAuthenticationToken token,
            RequestCreditDocuments request
    ) throws IOException {

        var user = this.userClient.findUserWithId(token.getName());

        if (!request.cpf().equals(user.cpf())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "Bad request", "This CPF was not the one used when creating the account. Process denied."
            ));
        }

        // Valida presença do comprovante de renda
        if (request.proofOfIncome().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Income file is required."));
        }

        // Define diretório para upload do arquivo
        String uploadDir = "C:\\Users\\rodri\\OneDrive\\Documentos\\negocios\\";

        // Salva comprovante de renda com nome único
        File incomeFile = new File(uploadDir + UUID.randomUUID() + "_" + request.proofOfIncome().getOriginalFilename());
        request.proofOfIncome().transferTo(incomeFile);

        // Cria evento com dados para análise de crédito
        var event = new EventCreditDocuments(
                token.getName(),
                request.fullName(),
                request.cpf(),
                request.date(),
                request.occupation(),
                request.salary(),
                incomeFile.getAbsolutePath()
        );

        // Envia dados para análise de crédito via Kafka
        log.info("Request sent to Kafka: {}", event);
        kafkaTemplate.send("credit-documents-analysis-topic", event);

        return ResponseEntity.accepted().body(Map.of("Accepted", "Your data has been sent for analysis"));
    }

    /**
     * Verifica o status dos documentos de crédito do usuário
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com status dos documentos de crédito:
     *         - "SEND": Usuário precisa enviar documentos
     *         - "PENDING": Documentos em análise
     *         - "APPROVED": Documentos aprovados
     *         - "REJECTED": Documentos rejeitados
     * @security Acesso restrito a usuários autenticados
     */
    public ResponseEntity<Map<String, String>> checkCreditDocumentStatus(JwtAuthenticationToken token){

        var documents = this.creditDocumentRepository
                .findByUserId(token.getName());

        if (documents.isEmpty()) {
            return ResponseEntity.ok(Map.of("STATUS","SEND"));
        }

        if (documents.get().getStatus().equals(Status.PENDING)) {
            return ResponseEntity.ok(Map.of("STATUS","PENDING"));
        } else if (documents.get().getStatus().equals(Status.APPROVED)) {
            return ResponseEntity.ok(Map.of("STATUS","APPROVED"));
        } else if (documents.get().getStatus().equals(Status.REJECTED)) {
            return ResponseEntity.ok(Map.of("STATUS","REJECTED"));
        }

        return ResponseEntity.ok().build();
    }

    //*********************************** PAGE 5 - OPERAÇÕES ADMINISTRATIVAS ***********************************

    /**
     * Busca todos os documentos de crédito cadastrados no sistema
     *
     * @return ResponseEntity com lista de DTOs contendo dados de todos os documentos de crédito
     * @security Acesso restrito a administradores
     * @apiNote Utilizado para painel administrativo de análise de crédito
     */
    public ResponseEntity<List<ResponseCreditDocuments>> findAllDocuments() {
        List<CreditDocument> documents = this.creditDocumentRepository.findAll();

        List<ResponseCreditDocuments> response = documents.stream()
                .map(document -> new ResponseCreditDocuments(
                        document.getCreditDocumentId(),
                        document.getCpf(),
                        document.getDate(),
                        document.getOccupation(),
                        document.getSalary(),
                        document.getIncomeFile(),
                        document.getStatus()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Aprova um documento de crédito e dispara evento para liberação de limite
     *
     * @param request DTO contendo ID do documento de crédito a ser aprovado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @throws Transactional Garante atomicidade na operação
     * @security Acesso restrito a administradores
     * @kafka Dispara evento para liberação de limite no tópico "approved-limit-card-topic"
     */
    @Transactional
    public ResponseEntity<Void> approveCreditDocument(RequestApproveCreditDocument request){

        Optional<CreditDocument> document = this.creditDocumentRepository
                .findById(request.creditDocumentId());

        if (document.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        document.get().setStatus(Status.APPROVED);
        this.creditDocumentRepository.save(document.get());
        this.kafkaTemplate.send("approved-limit-card-topic", new SendCreditLimitApproval(
                document.get().getUserId(),
                document.get().getSalary()
        ));
        return ResponseEntity.ok().build();
    }

    /**
     * Rejeita um documento de crédito e dispara evento de notificação
     *
     * @param request DTO contendo ID do documento de crédito a ser rejeitado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @throws Transactional Garante atomicidade na operação
     * @security Acesso restrito a administradores
     * @kafka Dispara evento de rejeição no tópico "rejected-limit-card-topic"
     * @note Vai direto para o serviço de notificação, não passa pelo serviço de cartão
     */
    @Transactional
    public ResponseEntity<Void> rejectCreditDocument(RequestRejectCreditDocument request){

        Optional<CreditDocument> document = this.creditDocumentRepository
                .findById(request.creditDocumentId());

        if (document.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        document.get().setStatus(Status.REJECTED);
        this.creditDocumentRepository.save(document.get());
        this.kafkaTemplate.send("rejected-limit-card-topic", new SendCreditLimitRejected(
                document.get().getUserId())
        );

        return ResponseEntity.ok().build();
    }

    /**
     * Consulta o limite de crédito baseado na renda do usuário
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com o valor do limite de crédito (baseado na renda)
     * @security Acesso restrito a usuários autenticados
     * @note O limite é calculado com base no salário informado nos documentos
     */
    public ResponseEntity<Double> limitOfCredit(JwtAuthenticationToken token) {
        Optional<CreditDocument> creditDocument = this.creditDocumentRepository.findByUserId(token.getName());

        if (creditDocument.isEmpty()){
            return ResponseEntity.ok().build();
        }

        return creditDocument.map(document -> ResponseEntity.ok(creditDocument.get().getSalary()))
                .orElseThrow();
    }
}