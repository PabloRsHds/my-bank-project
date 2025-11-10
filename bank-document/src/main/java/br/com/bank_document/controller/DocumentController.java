package br.com.bank_document.controller;

import br.com.bank_document.dtos.document.RequestApproveDocument;
import br.com.bank_document.dtos.document.RequestDocuments;
import br.com.bank_document.dtos.document.RequestRejectDocument;
import br.com.bank_document.dtos.document.ResponseDocuments;
import br.com.bank_document.services.DocumentService;
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
 * Controlador REST para operações relacionadas a documentos cadastrais
 * Expõe endpoints para usuários e administradores gerenciarem documentos de identificação e endereço
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o caminho base "/api" para todos os endpoints
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Construtor para injeção de dependência do DocumentService
     * @param service Serviço de documentos cadastrais contendo a lógica de negócio
     */
    @Autowired
    public DocumentController(DocumentService service){
        this.documentService = service;
    }


    /**
     * Endpoint para envio de documentos para análise de conta/cartão
     *
     * @param token Token JWT de autenticação
     * @param request DTO com documentos e informações pessoais
     * @return ResponseEntity com confirmação do envio para análise
     * @throws IOException Em caso de erro no processamento dos arquivos
     * @consumes MULTIPART_FORM_DATA Para recebimento de arquivos
     */
    @PostMapping(value = "/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> documentsAnalysis(
            JwtAuthenticationToken token,
            @ModelAttribute RequestDocuments request
    ) throws IOException {
        return this.documentService.documentsForAnalysis(token, request);
    }

    /**
     * Endpoint para listar todos os documentos cadastrais do sistema
     *
     * @return ResponseEntity com lista de todos os documentos cadastrais
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @GetMapping("/documents")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<ResponseDocuments>> findAllDocuments(){
        return this.documentService.findAllDocuments();
    }

    /**
     * Endpoint para verificar o status dos documentos cadastrais do usuário autenticado
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com status dos documentos cadastrais
     * @security Acesso restrito a usuários autenticados
     */
    @GetMapping("/check-document-status")
    public ResponseEntity<Map<String, String>> checkDocumentStatus(JwtAuthenticationToken token){
        return this.documentService.checkDocumentStatus(token);
    }

    /**
     * Endpoint para aprovar um documento cadastral
     *
     * @param request DTO contendo ID do documento a ser aprovado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @PutMapping("/approve-document")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> approveDocument(@RequestBody RequestApproveDocument request){
        return this.documentService.approveDocument(request);
    }

    /**
     * Endpoint para rejeitar um documento cadastral
     *
     * @param request DTO contendo ID do documento a ser rejeitado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @PutMapping("/reject-document")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> rejectDocument(@RequestBody RequestRejectDocument request){
        return this.documentService.rejectDocument(request);
    }
}