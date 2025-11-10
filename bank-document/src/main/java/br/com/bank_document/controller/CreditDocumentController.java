package br.com.bank_document.controller;

import br.com.bank_document.dtos.creditDocument.RequestApproveCreditDocument;
import br.com.bank_document.dtos.creditDocument.RequestCreditDocuments;
import br.com.bank_document.dtos.creditDocument.RequestRejectCreditDocument;
import br.com.bank_document.dtos.creditDocument.ResponseCreditDocuments;
import br.com.bank_document.services.creditService.CreditDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operações relacionadas a documentos de crédito
 * Expõe endpoints para usuários e administradores gerenciarem solicitações de limite de crédito
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o caminho base "/api" para todos os endpoints
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class CreditDocumentController {

    private final CreditDocumentService creditDocumentService;

    /**
     * Construtor para injeção de dependência do CreditDocumentService
     * @param service Serviço de documentos de crédito contendo a lógica de negócio
     */
    @Autowired
    public CreditDocumentController(CreditDocumentService service){
        this.creditDocumentService = service;
    }


    /**
     * Endpoint para envio de documentos para solicitação de limite de crédito
     *
     * @param token Token JWT de autenticação
     * @param request DTO com documentos e informações profissionais
     * @return ResponseEntity com confirmação do envio para análise
     * @throws IOException Em caso de erro no processamento dos arquivos
     * @consumes MULTIPART_FORM_DATA Para recebimento de arquivos
     */
    @PostMapping(value = "/credit-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> creditDocumentsAnalysis(
            JwtAuthenticationToken token,
            @ModelAttribute RequestCreditDocuments request
    ) throws IOException {
        return this.creditDocumentService.creditDocumentsForAnalysis(token, request);
    }


    /**
     * Endpoint para verificar o status dos documentos de crédito do usuário autenticado
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com status dos documentos de crédito
     * @security Acesso restrito a usuários autenticados
     */
    @GetMapping("/check-credit-document-status")
    public ResponseEntity<Map<String, String>> checkCreditDocumentStatus(JwtAuthenticationToken token){
        return this.creditDocumentService.checkCreditDocumentStatus(token);
    }

    /**
     * Endpoint para consultar o limite de crédito baseado na renda do usuário
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com o valor do limite de crédito (baseado na renda)
     * @security Acesso restrito a usuários autenticados
     */
    @GetMapping("/limit-credit")
    public ResponseEntity<Double> limitOfCredit(JwtAuthenticationToken token) {
        return this.creditDocumentService.limitOfCredit(token);
    }

    /**
     * Endpoint para listar todos os documentos de crédito do sistema
     *
     * @return ResponseEntity com lista de todos os documentos de crédito
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @GetMapping("/credit-documents")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<ResponseCreditDocuments>> findAllDocuments(){
        return this.creditDocumentService.findAllDocuments();
    }

    /**
     * Endpoint para aprovar um documento de crédito
     *
     * @param request DTO contendo ID do documento de crédito a ser aprovado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @PutMapping("/approve-credit-document")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> approveCreditDocument(@RequestBody RequestApproveCreditDocument request){
        return this.creditDocumentService.approveCreditDocument(request);
    }

    /**
     * Endpoint para rejeitar um documento de crédito
     *
     * @param request DTO contendo ID do documento de crédito a ser rejeitado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     * @note O nome do método no controlador está como "rejectRejectedDocument" mas delega para "rejectCreditDocument" no serviço
     */
    @PutMapping("/reject-credit-document")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> rejectRejectedDocument(@RequestBody RequestRejectCreditDocument request){
        return this.creditDocumentService.rejectCreditDocument(request);
    }
}