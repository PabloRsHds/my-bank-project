package br.com.bank_document.services;

import br.com.bank_document.dtos.RequestApproveDocument;
import br.com.bank_document.dtos.RequestRejectDocument;
import br.com.bank_document.dtos.ResponseDocuments;
import br.com.bank_document.dtos.SendCardEvent;
import br.com.bank_document.enums.Status;
import br.com.bank_document.models.Document;
import br.com.bank_document.repositories.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de documentos cadastrais no sistema bancário
 * Responsável por operações de verificação, aprovação e rejeição de documentos
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final KafkaTemplate<String, SendCardEvent> kafkaTemplate;

    /**
     * Construtor para injeção de dependências
     * @param repository Repositório para operações de banco de dados
     * @param kafkaTemplate Template para comunicação assíncrona via Kafka
     */
    @Autowired
    public DocumentService(
            DocumentRepository repository,
            KafkaTemplate<String, SendCardEvent> kafkaTemplate){
        this.documentRepository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    //*********************************** PAGE 4 - OPERAÇÕES DO USUÁRIO ***********************************

    /**
     * Verifica se o usuário possui um cartão aprovado com base nos documentos
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com true se usuário possui cartão aprovado, false caso contrário
     * @security Acesso restrito a usuários autenticados
     * @note Utiliza o ID do usuário extraído do token JWT
     */
    public ResponseEntity<Boolean> verifyIfUserHasCard(JwtAuthenticationToken token){
        Optional<Document> document = this.documentRepository
                .findByUserId(token.getName());

        if (document.get().getStatus().equals(Status.APPROVED)) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.ok(false);
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