package br.com.bank_user.controller;

import br.com.bank_user.dtos.block_user.ActiveUserWithCpf;
import br.com.bank_user.dtos.block_user.BlockUserWithCpf;
import br.com.bank_user.dtos.register_user.ResponseUsersDto;
import br.com.bank_user.service.admService.AdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operações administrativas de gerenciamento de usuários
 * Expõe endpoints exclusivos para administradores do sistema
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o caminho base "/api/adm" para endpoints administrativos
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api/adm")
public class AdmController {

    private final AdmService admService;

    /**
     * Construtor para injeção de dependência do AdmService
     * @param admService Serviço administrativo contendo a lógica de negócio
     */
    @Autowired
    public AdmController(AdmService admService){
        this.admService = admService;
    }

    /**
     * Endpoint para listar todos os usuários cadastrados no sistema
     * Retorna informações completas incluindo dados sensíveis (apenas para admins)
     *
     * @return ResponseEntity com lista de DTOs contendo dados de todos os usuários
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @GetMapping("/get-all-users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<ResponseUsersDto>> getAllUsers() {
        return this.admService.getAllUsers();
    }

    /**
     * Endpoint para ativar uma conta de usuário previamente bloqueada
     *
     * @param request DTO contendo CPF do usuário a ser ativado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @PutMapping("/active-user")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> activeUser(@RequestBody ActiveUserWithCpf request) {
        return this.admService.activeUser(request);
    }

    /**
     * Endpoint para bloquear uma conta de usuário ativa
     *
     * @param request DTO contendo CPF do usuário a ser bloqueado
     * @return ResponseEntity vazio com status 200 em caso de sucesso
     * @preAuthorize Restringe acesso apenas a usuários com escopo ADMIN
     * @security Requer token JWT com autoridade SCOPE_ADMIN
     */
    @PutMapping("/block-user")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> blockUser(@RequestBody BlockUserWithCpf request) {
        return this.admService.blockUser(request);
    }
}