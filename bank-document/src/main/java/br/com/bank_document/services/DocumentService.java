package br.com.bank_document.services;

import br.com.bank_document.dtos.card.SendCardEvent;
import br.com.bank_document.dtos.document.*;
import br.com.bank_document.enums.Status;
import br.com.bank_document.models.Document;
import br.com.bank_document.repositories.DocumentRepository;
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
 * Serviço para gerenciamento de documentos cadastrais no sistema bancário
 * Responsável por operações de verificação, aprovação e rejeição de documentos
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Slf4j
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Construtor para injeção de dependências
     * @param repository Repositório para operações de banco de dados
     * @param kafkaTemplate Template para comunicação assíncrona via Kafka
     */
    @Autowired
    public DocumentService(
            DocumentRepository repository,
            KafkaTemplate<String, Object> kafkaTemplate){
        this.documentRepository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    //*********************************** PAGE 4 - OPERAÇÕES DO USUÁRIO ***********************************


    /**
     * Processa documentos para análise de conta
     * Valida e armazena comprovante de endereço e renda
     *
     * @param token Token JWT de autenticação
     * @param request DTO com documentos e informações pessoais
     * @return ResponseEntity com confirmação do envio para análise
     * @throws IOException Em caso de erro no armazenamento dos arquivos
     * @apiNote Envia documentos para análise via Kafka
     */
    public ResponseEntity<Map<String, String>> documentsForAnalysis(
            JwtAuthenticationToken token,
            RequestDocuments request
    ) throws IOException {

        // Valida presença dos arquivos obrigatórios
        if (request.proofOfAddress().isEmpty() || request.proofOfIncome().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Both files are required."));
        }

        // Define diretório para upload dos arquivos
        String uploadDir = "C:\\Users\\rodri\\OneDrive\\Documentos\\negocios\\";

        // Salva comprovante de endereço com nome único
        File addressFile = new File(uploadDir + UUID.randomUUID() + "_" + request.proofOfAddress().getOriginalFilename());
        request.proofOfAddress().transferTo(addressFile);

        // Salva comprovante de renda com nome único
        File incomeFile = new File(uploadDir + UUID.randomUUID() + "_" + request.proofOfIncome().getOriginalFilename());
        request.proofOfIncome().transferTo(incomeFile);

        // Cria evento com dados para análise
        var event = new EventDocuments(
                token.getName(),
                request.fullName(),
                request.rg(),
                request.cpf(),
                addressFile.getAbsolutePath(),
                incomeFile.getAbsolutePath()
        );

        // Envia documentos para análise via Kafka
        kafkaTemplate.send("documents-analysis-topic", event);

        return ResponseEntity.accepted().body(Map.of("Accepted", "Your data has been sent for analysis"));
    }

    /**
     * Verifica o status atual dos documentos do usuário para criação de cartão
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com status dos documentos:
     *         - "SEND": Usuário precisa enviar documentos
     *         - "PENDING": Documentos em análise
     *         - "APPROVED": Documentos aprovados
     *         - "REJECTED": Documentos rejeitados
     * @security Acesso restrito a usuários autenticados
     */
    public ResponseEntity<Map<String, String>> checkDocumentStatus(JwtAuthenticationToken token){

        var documents = this.documentRepository
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

    //*********************************************************************************

    //*********************************** PAGE 5 - OPERAÇÕES ADMINISTRATIVAS ***********************************

    /**
     * Busca todos os documentos cadastrados no sistema
     *
     * @return ResponseEntity com lista de DTOs contendo dados de todos os documentos
     * @security Acesso restrito a administradores
     * @apiNote Utilizado para painel administrativo de análise de documentos
     */
    public ResponseEntity<List<ResponseDocuments>> findAllDocuments() {
        List<Document> documents = this.documentRepository.findAll();

        List<ResponseDocuments> response = documents.stream()
                .map(document -> new ResponseDocuments(
                        document.getDocumentId(),
                        document.getRg(),
                        document.getCpf(),
                        document.getAddressFile(),
                        document.getIncomeFile(),
                        document.getStatus()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Aprova um documento e dispara evento para criação de cartão
     *
     * @param request DTO contendo ID do documento a ser aprovado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @throws jakarta.transaction.Transactional Garante atomicidade na operação
     * @security Acesso restrito a administradores
     * @kafka Dispara evento para criação de cartão no tópico "approved-card-topic"
     */
    @Transactional
    public ResponseEntity<Void> approveDocument(RequestApproveDocument request){

        Optional<Document> document = this.documentRepository.findById(request.documentId());

        if (document.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        document.get().setStatus(Status.APPROVED);
        this.documentRepository.save(document.get());
        this.kafkaTemplate.send("approved-card-topic", new SendCardEvent(
                document.get().getUserId(),
                document.get().getFullName(),
                document.get().getRg(),
                document.get().getCpf()
        ));
        return ResponseEntity.ok().build();
    }

    /**
     * Rejeita um documento e dispara evento de cancelamento
     *
     * @param request DTO contendo ID do documento a ser rejeitado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @throws jakarta.transaction.Transactional Garante atomicidade na operação
     * @security Acesso restrito a administradores
     * @kafka Dispara evento de cancelamento no tópico "canceled-card-topic"
     */
    @Transactional
    public ResponseEntity<Void> rejectDocument(RequestRejectDocument request){

        Optional<Document> document = this.documentRepository.findById(request.documentId());

        if (document.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        document.get().setStatus(Status.REJECTED);
        this.documentRepository.save(document.get());
        this.kafkaTemplate.send("canceled-card-topic", new SendCardEvent(
                document.get().getUserId(),
                document.get().getFullName(),
                document.get().getRg(),
                document.get().getCpf()
        ));
        return ResponseEntity.ok().build();
    }

    //*********************************************************************************
}