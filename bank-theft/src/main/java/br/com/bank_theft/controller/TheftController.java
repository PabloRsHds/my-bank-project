package br.com.bank_theft.controller;

import br.com.bank_theft.dtos.RequestTheftDto;
import br.com.bank_theft.dtos.ResponseReports;
import br.com.bank_theft.services.TheftService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operações relacionadas a relatos de roubo e furto
 * Expõe endpoints para usuários reportarem incidentes e administradores consultarem relatórios
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o caminho base "/api" para todos os endpoints
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class TheftController {

    private final TheftService theftService;

    /**
     * Construtor para injeção de dependência do TheftService
     * @param service Serviço de roubos contendo a lógica de negócio
     */
    public TheftController(TheftService service) {
        this.theftService = service;
    }

    /**
     * Endpoint para listar todos os relatos de roubo do sistema
     *
     * @return ResponseEntity com lista de todos os relatos de roubo
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     * @apiNote Utilizado para painel administrativo de análise de incidentes de segurança
     */
    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<ResponseReports>> findAllReports() {
        return this.theftService.findAllReports();
    }

    /**
     * Endpoint para reportar um novo roubo ou furto
     *
     * @param request DTO contendo dados do incidente reportado
     * @return ResponseEntity com mensagem de confirmação do registro
     * @valid Ativa validação dos dados de entrada baseado nas annotations do DTO
     * @security Acesso permitido para usuários autenticados (não requer escopo específico)
     * @httpMethod POST para criação de novo recurso no sistema
     */
    @PostMapping("/report-theft")
    public ResponseEntity<Map<String, String>> reportTheft(@Valid @RequestBody RequestTheftDto request){
        return this.theftService.reportTheft(request);
    }
}