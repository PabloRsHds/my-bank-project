package br.com.bank_login.services;

import br.com.bank_login.dtos.RequestLoginDto;
import br.com.bank_login.dtos.RequestTokensDto;
import br.com.bank_login.dtos.ResponseLoginHistory;
import br.com.bank_login.dtos.ResponseTokens;
import br.com.bank_login.microservices.user.UserClient;
import br.com.bank_login.model.Login;
import br.com.bank_login.repository.LoginRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Serviço principal para autenticação e gerenciamento de tokens JWT
 * Responsável por operações de login, refresh de tokens e histórico de acessos
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private final UserClient userClient;
    private final LoginRepository loginRepository;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final CircuitBreakerFactory<?,?> circuitBreakerFactory;

    /**
     * Construtor para injeção de dependências do serviço de login
     *
     * @param client Cliente Feign para comunicação com microserviço de usuários
     * @param repository Repositório para operações de banco de dados de logins
     * @param jwt Encoder para geração de tokens JWT
     * @param jwtD Decoder para validação de tokens JWT
     * @param circuit Factory para pattern Circuit Breaker
     */
    public LoginService(UserClient client,
                        LoginRepository repository,
                        JwtEncoder jwt,
                        JwtDecoder jwtD,
                        CircuitBreakerFactory<?,?> circuit){
        this.userClient = client;
        this.loginRepository = repository;
        this.jwtEncoder = jwt;
        this.jwtDecoder = jwtD;
        this.circuitBreakerFactory = circuit;
    }

    /**
     * Processa o login do usuário no sistema bancário
     * Realiza autenticação e gera tokens de acesso e refresh
     *
     * @param request DTO com credenciais de login (CPF e senha)
     * @return ResponseEntity com tokens JWT ou mensagem de erro
     *
     * @implSpec Fluxo de autenticação:
     * 1. Valida existência do CPF
     * 2. Verifica status da conta (bloqueada/verificada)
     * 3. Valida senha criptografada
     * 4. Gera tokens JWT com expiração
     * 5. Registra acesso no histórico
     */
    public ResponseEntity<Map<String, String>> login(@Valid RequestLoginDto request) {

        return circuitBreakerFactory.create("loginCB").run(
                () -> {
                    var cpf = this.userClient.findByCpf(request.cpf());
                    var userId = this.userClient.findByIdWithCpf(cpf);
                    var scopes = this.userClient.findByRoleWithCpf(cpf);
                    var status = this.userClient.findByStatusWithCpf(cpf);

                    if (cpf.isBlank()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Cpf or password is incorrect"));
                    }

                    if (Objects.equals(status,"BLOCKED")) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("unauthorized", "The user has been banned! send an email to the bank to find out why."));
                    }

                    //Verifico se o usuário não tem o e-mail verificado.
                    if (!this.userClient.verifyEmailWithCpf(cpf)){
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of("message", "Unverified user"));
                    }

                    //Verifico se o e-mail ou senha estão incorretos
                    if (!this.userClient.verifyPasswordEncode(request.cpf(), request.password())){
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Cpf or password is incorrect"));
                    }

                    var expireToken = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
                    var now = Instant.now();

                    //Criando o token, com apenas 1 hora de validação.
                    var claims = JwtClaimsSet.builder()
                            .issuer("PICPAY") //nome da aplicação
                            .issuedAt(now)//horario que foi criado o token
                            .subject(userId)//o token vai estar ligado ao id do usuario
                            .expiresAt(expireToken)//o token vai expirar em 1 hora
                            .claim("scope", scopes)//o token vai ter o scope do usuario
                            .build();//criando o token

                    var expireRefreshToken = LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));
                    //Criando o refresh-token, com 30 dias de validação
                    var claimsRefresh = JwtClaimsSet.builder()
                            .issuer("BACKEND-LOGIN")
                            .issuedAt(now)
                            .subject(userId)
                            .expiresAt(expireRefreshToken)
                            .claim("scope", scopes)
                            .build();

                    //gero o token
                    var accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                    //gero o refresh-token
                    var accessRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claimsRefresh)).getTokenValue();
                    //retorna o token e o refresh-token

                    //Crio o loginEntity
                    var loginEntity = new Login();
                    loginEntity.setUserId(userId);
                    this.loginRepository.save(loginEntity);

                    return ResponseEntity.ok().body(Map.of("accessToken", accessToken, "refreshToken", accessRefreshToken));
                },
                throwable -> {
                    log.error("Login service is down, please try again later");
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(Map.of("error", "login service is down, please try again later"));
                }
        );
    }

    /**
     * Renova os tokens de acesso usando o refresh token válido
     * Gera novos tokens quando o access token expira mas o refresh token ainda é válido
     *
     * @param request DTO contendo access token e refresh token atuais
     * @return ResponseEntity com novos tokens ou status 401 se inválido
     */
    public ResponseEntity<ResponseTokens> refreshTokens(RequestTokensDto request){

        var accessToken = this.jwtDecoder.decode(request.accessToken());
        var refreshToken = this.jwtDecoder.decode(request.refreshToken());
        // Obtém a data de expiração do token (ajuste conforme sua implementação)
        Instant expireAccessTokenAt = accessToken.getExpiresAt();
        Instant expireRefreshTokenAt = refreshToken.getExpiresAt();

        if (refreshToken.getExpiresAt() != null && Instant.now().isBefore(refreshToken.getExpiresAt())) {

            //Obtendo o cpf do token
            var cpf = this.userClient.findCpfWithId(accessToken.getSubject());
            //Obtendo o id do usuário com o cpf
            var userId = this.userClient.findByIdWithCpf(cpf);
            //Obtendo o scope do usuário com o cpf
            var scopes = this.userClient.findByRoleWithCpf(cpf);

            var expireToken = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
            var now = Instant.now();

            //Criando o token, com apenas 1 hora de validação.
            var claims = JwtClaimsSet.builder()
                    .issuer("PICPAY") //nome da aplicação
                    .issuedAt(now)//horario que foi criado o token
                    .subject(userId)//o token vai estar ligado ao id do usuario
                    .expiresAt(expireToken)//o token vai expirar em 1 hora
                    .claim("scope", scopes)//o token vai ter o scope do usuario
                    .build();//criando o token

            var expireRefreshToken = LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));
            //Criando o refresh-token, com 30 dias de validação
            var claimsRefresh = JwtClaimsSet.builder()
                    .issuer("PICPAY")
                    .issuedAt(now)
                    .subject(userId)
                    .expiresAt(expireRefreshToken)
                    .claim("scope", scopes)
                    .build();

            //gero o token
            var newAccessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            //gero o refresh-token
            var newRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claimsRefresh)).getTokenValue();

            return ResponseEntity.ok(new ResponseTokens(newAccessToken, newRefreshToken));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Recupera o histórico de logins do usuário autenticado
     * Retorna os 5 acessos mais recentes ordenados por data
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com lista do histórico de acessos
     */
    public ResponseEntity<List<ResponseLoginHistory>> loginHistory(JwtAuthenticationToken token) {

        var logins = this.loginRepository.findAllByUserId(token.getName())
                .stream()
                .sorted(Comparator.comparing(Login::getTimeStamp).reversed())
                .map(login -> new ResponseLoginHistory(login.getTimeStamp()))
                .limit(5)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(logins);
    }
}