package br.com.bank_user.controller;

import br.com.bank_user.dtos.email.RequestEmailDto;
import br.com.bank_user.dtos.email.ResendCodeDto;
import br.com.bank_user.dtos.register_user.RequestUserDto;
import br.com.bank_user.dtos.register_user.ResponseUserDto;
import br.com.bank_user.dtos.update_user.RequestPasswordUpdate;
import br.com.bank_user.dtos.update_user.RequestPhoneUpdate;
import br.com.bank_user.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para operações de gerenciamento de usuários
 * Expõe endpoints para registro, verificação, atualização e exclusão de contas
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o caminho base "/api" para todos os endpoints
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     * Construtor para injeção de dependência do UserService
     * @param service Serviço de usuário contendo a lógica de negócio
     */
    @Autowired
    public UserController(UserService service){
        this.userService = service;
    }

    // ******************************* Página 1 - Registro *********************************************

    /**
     * Endpoint para registro de novo usuário no sistema
     *
     * @param request DTO com dados de registro do usuário
     * @return ResponseEntity com mensagem de sucesso ou erro
     * @valid Ativa validação dos dados de entrada baseado nas annotations do DTO
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RequestUserDto request){
        return this.userService.register(request);
    }

    // --------------------------------------------------------------------------------------

    // ******************************* Página 2 - Verificação de Email *********************************************

    /**
     * Endpoint para verificar se o email do usuário já foi verificado
     *
     * @param email Email do usuário a ser verificado
     * @return "true" se verificado, "false" caso contrário
     */
    @GetMapping("/check-email-verification")
    public String checkUserEmailVerification(@RequestParam String email) {
        return this.userService.checkUserEmailVerification(email);
    }

    /**
     * Endpoint para validar código de verificação de email
     *
     * @param request DTO contendo email e código de verificação
     * @return ResponseEntity com resultado da verificação
     */
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody RequestEmailDto request){
        return this.userService.verifyEmail(request);
    }

    /**
     * Endpoint para solicitar reenvio de código de verificação
     *
     * @param request DTO contendo email para reenvio
     * @return ResponseEntity com confirmação do reenvio
     */
    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(@RequestBody ResendCodeDto request){
        return this.userService.resendCode(request);
    }

    // --------------------------------------------------------------------------------------

    // ******************************* Página 3 - Consultas Básicas *********************************************

    /**
     * Endpoint para recuperar ID do usuário baseado no CPF
     *
     * @param cpf CPF do usuário
     * @return ResponseEntity com ID do usuário ou string vazia
     */
    @GetMapping("/get-id-with-cpf")
    public ResponseEntity<String> getIdWithCpf(@RequestParam String cpf) {
        return this.userService.getIdWithCpf(cpf);
    }

    // --------------------------------------------------------------------------------------

    // ******************************* Página 4 - Gerenciamento de Conta *********************************************

    /**
     * Endpoint para verificar se usuário possui privilégios de administrador
     *
     * @param token Token JWT de autenticação
     * @return ResponseEntity com true se for admin, false caso contrário
     * @security Requer autenticação JWT válida
     */
    @GetMapping("/verify-if-user-admin")
    public ResponseEntity<Boolean> roleAdmin(JwtAuthenticationToken token){
        return this.userService.verifyIfUserIsAdmin(token);
    }

    /**
     * Endpoint para verificar se usuário está completamente autenticado como cliente
     *
     * @param token Token JWT de autenticação
     * @return ResponseEntity com true se autenticado, false caso contrário
     * @security Requer autenticação JWT válida
     */
    @GetMapping("/check-user-verification")
    public ResponseEntity<Boolean> checkUserVerification(JwtAuthenticationToken token) {
        return this.userService.checkUserVerification(token);
    }

    /**
     * Endpoint para recuperar dados completos do usuário
     *
     * @param userId ID único do usuário
     * @return ResponseEntity com DTO contendo dados do usuário
     */
    @GetMapping("/get-user-with-id")
    public ResponseEntity<ResponseUserDto> getUserWithId(@RequestParam String userId) {
        return this.userService.getUserWithId(userId);
    }

    /**
     * Recupera o nome completo do usuário pelo ID
     *
     * @param userId ID único do usuário (UUID)
     * @return Nome completo do usuário ou null se não encontrado
     * @apiNote Utilizado para exibir informações do usuário logado
     */
    @GetMapping("/full-name")
    public String findByNameWithId(@RequestParam String userId) {
        return this.userService.findByNameWithId(userId);
    }

    /**
     * Endpoint para atualizar senha do usuário
     *
     * @param accessToken Token JWT de autenticação
     * @param request DTO com senha antiga e nova senha
     * @return ResponseEntity vazio com status apropriado
     * @security Requer validação da senha atual
     */
    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(JwtAuthenticationToken accessToken,
                                               @Valid @RequestBody RequestPasswordUpdate request){
        return this.userService.updatePassword(accessToken,request);
    }

    /**
     * Endpoint para atualizar número de telefone do usuário
     *
     * @param accessToken Token JWT de autenticação
     * @param request DTO com novo número de telefone
     * @return ResponseEntity vazio com status apropriado
     */
    @PutMapping("/update-phone")
    public ResponseEntity<Void> updatePhone(JwtAuthenticationToken accessToken,
                                            @Valid @RequestBody RequestPhoneUpdate request){
        return this.userService.updatePhone(accessToken,request);
    }

    /**
     * Endpoint para excluir permanentemente a conta do usuário
     *
     * @param token Token JWT de autenticação
     * @return ResponseEntity vazio com status 204 (No Content)
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(JwtAuthenticationToken token){
        return this.userService.deleteUser(token);
    }
    // --------------------------------------------------------------------------------------
}