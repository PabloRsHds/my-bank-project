package br.com.bank_login.controllers;

import br.com.bank_login.dtos.login.RequestLoginDto;
import br.com.bank_login.dtos.login.RequestTokensDto;
import br.com.bank_login.dtos.login.ResponseLoginHistory;
import br.com.bank_login.dtos.login.ResponseTokens;
import br.com.bank_login.services.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operações de autenticação e gerenciamento de tokens
 * Expõe endpoints para login, renovação de tokens e consulta de histórico de acessos
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o prefixo base para todos os endpoints
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;

    /**
     * Construtor para injeção de dependências do serviço de login
     *
     * @param service Serviço com lógica de negócio para autenticação
     */
    public LoginController(LoginService service){
        this.loginService = service;
    }

    /**
     * Endpoint para autenticação de usuários no sistema
     * Valida credenciais e retorna tokens JWT de acesso e refresh
     *
     * @param request DTO contendo CPF e senha do usuário
     * @return ResponseEntity com tokens JWT ou mensagem de erro
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody RequestLoginDto request){
        return this.loginService.login(request);
    }

    /**
     * Endpoint para renovação de tokens JWT expirados
     * Utiliza refresh token válido para gerar novos tokens de acesso
     *
     * @param request DTO contendo access token e refresh token atuais
     * @return ResponseEntity com novos tokens ou status 401 se inválido
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseTokens> refreshToken(@RequestBody RequestTokensDto request){
        return this.loginService.refreshTokens(request);
    }

    /**
     * Endpoint para consulta do histórico de acessos do usuário
     * Retorna os 5 logins mais recentes do usuário autenticado
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista do histórico de acessos
     */
    @GetMapping("/login-history")
    public ResponseEntity<List<ResponseLoginHistory>> loginHistory(JwtAuthenticationToken token) {
        return this.loginService.loginHistory(token);
    }
}