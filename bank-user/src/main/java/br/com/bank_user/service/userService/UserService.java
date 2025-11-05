package br.com.bank_user.service.userService;

import br.com.bank_user.dtos.delete_user.EventDeleteUser;
import br.com.bank_user.dtos.documents.EventCreditDocuments;
import br.com.bank_user.dtos.documents.EventDocuments;
import br.com.bank_user.dtos.documents.RequestCreditDocuments;
import br.com.bank_user.dtos.documents.RequestDocuments;
import br.com.bank_user.dtos.email.EmailVerificationEvent;
import br.com.bank_user.dtos.email.RequestEmailDto;
import br.com.bank_user.dtos.email.ResendCodeDto;
import br.com.bank_user.dtos.register_user.RequestUserDto;
import br.com.bank_user.dtos.register_user.ResponseUserDto;
import br.com.bank_user.dtos.update_user.RequestPasswordUpdate;
import br.com.bank_user.dtos.update_user.RequestPhoneUpdate;
import br.com.bank_user.dtos.user.EventWelcomeUser;
import br.com.bank_user.dtos.wallet.CreationWalletEvent;
import br.com.bank_user.enums.Role;
import br.com.bank_user.mapper.UserMapper;
import br.com.bank_user.model.User;
import br.com.bank_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Serviço principal para gerenciamento de usuários do sistema bancário
 * Responsável por operações de registro, verificação, atualização e exclusão de contas
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 * @slf4j Fornece logger para operações de logging
 *
 * @author Pablo R.
 */
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Construtor para injeção de dependências do serviço de usuário
     *
     * @param repository Repositório para operações de banco de dados
     * @param mapper Mapper para conversão entre DTOs e entidades
     * @param encoder Encoder para criptografia de senhas
     * @param circuitBreaker Factory para pattern Circuit Breaker
     * @param kafka Template para comunicação assíncrona via Kafka
     */
    @Autowired
    public UserService(UserRepository repository,
                       UserMapper mapper,
                       PasswordEncoder encoder,
                       CircuitBreakerFactory<?, ?> circuitBreaker,
                       KafkaTemplate<String, Object> kafka){
        this.userRepository = repository;
        this.userMapper = mapper;
        this.passwordEncoder = encoder;
        this.circuitBreakerFactory = circuitBreaker;
        this.kafkaTemplate = kafka;
    }

    /**
     * Registra um novo usuário no sistema bancário
     * Realiza verificações de duplicidade e envia código de verificação por email
     *
     * @param request DTO com dados de registro do usuário
     * @return ResponseEntity com mensagem de sucesso ou erro
     * @throws jakarta.transaction.Transactional Garante atomicidade na operação
     *
     * @implSpec Fluxo de registro:
     * 1. Valida duplicidade de CPF, email e telefone
     * 2. Gera código de verificação com expiração
     * 3. Criptografa senha
     * 4. Envia código por email via Kafka
     * 5. Salva usuário no banco
     *
     * @example
     * RequestUserDto(
     *   cpf="123.456.789-00",
     *   email="user@example.com",
     *   phone="11999999999",
     *   password="Senha123@"
     * )
     */
    @Transactional
    public ResponseEntity<Map<String,String>> register(RequestUserDto request){

        // Verifica se CPF, email ou telefone já estão cadastrados
        Optional<User> cpf = this.userRepository.findByCpf(request.cpf());
        Optional<User> email = this.userRepository.findByEmail(request.email());
        Optional<User> phone = this.userRepository.findByPhone(request.phone());

        if (cpf.isPresent()){
            log.warn("This cpf already cadastred in database");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Bad request", "This cpf already cadastred"
            ));
        } else if (email.isPresent()) {
            log.warn("This e-mail already cadastred in database");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Bad request", "This e-mail already cadastred"
            ));
        } else if (phone.isPresent()) {
            log.warn("This phone already cadastred in database");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Bad request", "This phone already cadastred"
            ));
        }

        // Gera código de verificação numérico de 6 dígitos
        var code = String.format("%06d", new Random().nextInt(100000));
        // Define expiração do código para 12 minutos
        var expireCode = LocalDateTime.now().plusMinutes(12);

        // Criptografa a senha do usuário
        var passwordEncode = this.passwordEncoder.encode(request.password());
        // Converte DTO para entidade User com os dados processados
        var userEntity = this.userMapper.toEntity(request,passwordEncode, code, expireCode);

        // Executa operação com circuit breaker para resiliência
        return this.circuitBreakerFactory.create("registerCB").run(
                () -> {
                    // Envia código de verificação por email via Kafka
                    this.kafkaTemplate.send("email-verification-topic",
                            new EmailVerificationEvent(request.email(), code));

                    // Salva usuário no banco de dados
                    this.userRepository.save(userEntity);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                            "message", "Verification email sent. Check your inbox!"
                    ));
                },
                throwable -> {
                    // Fallback em caso de falha no envio de email
                    log.error("Failed to send email: {}", throwable.getMessage());
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
                }
        );
    }

    /**
     * Verifica se o email do usuário já foi verificado
     *
     * @param email Email a ser verificado
     * @return "true" se verificado, "false" caso contrário
     * @apiNote Utilizado por outros serviços para validar estado do usuário
     */
    public String checkUserEmailVerification(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);

        return user.isPresent() && Boolean.TRUE.equals(user.get().getVerifyEmail())
                ? "true" : "false";
    }

    /**
     * Valida o código de verificação de email do usuário
     * Gerencia expiração do código e reenvia se necessário
     *
     * @param request DTO contendo email e código de verificação
     * @return ResponseEntity com resultado da verificação
     *
     * @implSpec Fluxo de verificação:
     * 1. Busca usuário por email
     * 2. Verifica se já está verificado
     * 3. Valida código e expiração
     * 4. Se expirado, reenvia código
     * 5. Se válido, ativa conta e dispara eventos
     */
    public ResponseEntity<Map<String, String>> verifyEmail(RequestEmailDto request){

        var user = this.userRepository.findByEmail(request.email());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        // Verifica se usuário já está verificado
        if (user.get().getVerifyEmail().equals(true)){
            System.out.println("User already verified");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "bad request","User already verified"));
        }

        // Valida se o código informado corresponde ao código gerado
        if (!user.get().getCode().equals(request.code())){
            log.error("the codes don't match: {} and {}", user.get().getCode(), request.code());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","This code is incorrect"));
        }

        // Verifica se o código expirou
        if (LocalDateTime.now().isAfter(user.get().getExpireCode())){
            user.get().setCode(null);
            var code = String.format("%05d",new Random().nextInt(100000));

            user.get().setCode(request.code());
            user.get().setExpireCode(LocalDateTime.now().plusMinutes(10));

            // Reenvia código expirado com circuit breaker
            return this.circuitBreakerFactory.create("kafkaProducerCB").run(
                    () -> {
                        this.kafkaTemplate.send("email-verification-topic", new EmailVerificationEvent(request.email(), code));
                        this.userRepository.save(user.get());
                        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                                "message", "Your verification code has expired, we have sent a message to your mail"));
                    },
                    throwable -> {
                        log.error("Failed to send email :( {}", throwable.getMessage());
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
                    }
            );
        } else {
            // Código válido - finaliza verificação
            user.get().setCode(null);
            user.get().setExpireCode(null);
            user.get().setVerifyEmail(true);
            this.userRepository.save(user.get());

            // Dispara eventos para criação de carteira e email de boas-vindas
            this.kafkaTemplate.send("creation-wallet-topic",
                    new CreationWalletEvent(user.get().getUserId()));

            this.kafkaTemplate.send("welcome-topic",
                    new EventWelcomeUser(user.get().getUserId()));

            return ResponseEntity.ok().body(Map.of("message", "email verified successfully"));
        }
    }

    /**
     * Reenvia código de verificação para o email do usuário
     *
     * @param request DTO contendo email para reenvio
     * @return ResponseEntity com confirmação do reenvio
     * @throws jakarta.transaction.Transactional Garante atomicidade
     */
    @Transactional
    public ResponseEntity<Map<String, String>> resendCode(ResendCodeDto request){

        Optional<User> user = this.userRepository.findByEmail(request.email());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "bad request", "This is email cannot be found"));
        }

        // Gera novo código com expiração de 10 minutos
        var code = String.format("%06d", new Random().nextInt(100000));
        user.get().setCode(code);
        user.get().setExpireCode(LocalDateTime.now().plusMinutes(10));

        // Envia novo código com proteção de circuit breaker
        return this.circuitBreakerFactory.create("kafkaProducerCB").run(
                () -> {
                    this.kafkaTemplate.send("email-verification-topic", new EmailVerificationEvent(request.email(), code));
                    this.userRepository.save(user.get());
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                            "message", "Verification email sent. Check your inbox!"));
                },
                throwable -> {
                    log.error("Failed to send email :(: {}", throwable.getMessage());
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
                }
        );
    }

    /**
     * Recupera o ID do usuário baseado no CPF
     *
     * @param cpf CPF do usuário
     * @return ResponseEntity com ID do usuário ou string vazia
     * @apiNote Utilizado para mapeamento entre serviços
     */
    public ResponseEntity<String> getIdWithCpf(String cpf) {
        Optional<User> user = this.userRepository.findByCpf(cpf);
        return ResponseEntity.ok(user.map(User::getUserId).orElse(""));
    }

    /**
     * Verifica se o usuário possui privilégios de administrador
     *
     * @param token Token JWT de autenticação
     * @return ResponseEntity com true se for admin, false caso contrário
     * @security Acesso restrito a usuários autenticados
     */
    public ResponseEntity<Boolean> verifyIfUserIsAdmin(JwtAuthenticationToken token){

        Optional<User> user = this.userRepository.findById(token.getName());

        if (user.get().getRole().equals(Role.USER)) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok().body(true);
    }

    /**
     * Verifica se o usuário está autenticado como cliente
     *
     * @param token Token JWT de autenticação
     * @return ResponseEntity com true se autenticado, false caso contrário
     * @apiNote Diferente de verifyEmail, verifica autenticação completa
     */
    public ResponseEntity<Boolean> checkUserVerification(JwtAuthenticationToken token) {
        Optional<User> user = this.userRepository.findById(token.getName());

        if (user.get().getAuthenticatedClient().equals(false)) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }

    /**
     * Recupera dados completos do usuário pelo ID
     *
     * @param userId ID único do usuário
     * @return ResponseEntity com DTO contendo dados do usuário
     */
    public ResponseEntity<ResponseUserDto> getUserWithId(String userId) {
        Optional<User> user = this.userRepository.findById(userId);
        return ResponseEntity.ok().body(user.map(user1 -> new ResponseUserDto(
                user.get().getCpf(),
                user.get().getFullName(),
                user.get().getEmail(),
                user.get().getPassword(),
                user.get().getPhone(),
                user.get().getDate().toString())).orElse(null));
    }

    /**
     * Atualiza a senha do usuário com validação da senha antiga
     *
     * @param accessToken Token JWT de autenticação
     * @param request DTO com senha antiga e nova senha
     * @return ResponseEntity vazio com status apropriado
     * @throws jakarta.transaction.Transactional Garante atomicidade
     * @security Requer validação da senha atual
     */
    @Transactional
    public ResponseEntity<Void> updatePassword(JwtAuthenticationToken accessToken, @Valid RequestPasswordUpdate request){

        var user = this.userRepository.findById(accessToken.getName())
                .orElseThrow(); // Recupera usuário do token JWT

        // Valida se a senha antiga corresponde à senha atual
        if (!this.passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Criptografa e salva nova senha
        user.setPassword(this.passwordEncoder.encode(request.password()));
        this.userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Atualiza o número de telefone do usuário
     *
     * @param accessToken Token JWT de autenticação
     * @param request DTO com novo número de telefone
     * @return ResponseEntity vazio com status apropriado
     * @throws jakarta.transaction.Transactional Garante atomicidade
     */
    @Transactional
    public ResponseEntity<Void> updatePhone(JwtAuthenticationToken accessToken, @Valid RequestPhoneUpdate request){

        var user = this.userRepository.findById(accessToken.getName())
                .orElseThrow(); // Recupera usuário do token JWT

        // Atualiza telefone se o novo valor não estiver em branco
        if (!request.phone().isBlank()){
            user.setPhone(request.phone());
        }

        this.userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    /**
     * Exclui permanentemente a conta do usuário
     * Dispara evento para limpeza de dados relacionados
     *
     * @param token Token JWT de autenticação
     * @return ResponseEntity vazio com status 204 (No Content)
     * @throws jakarta.transaction.Transactional Garante atomicidade
     * @implSpec Dispara evento Kafka para limpeza em outros serviços
     */
    @Transactional
    public ResponseEntity<Void> deleteUser(JwtAuthenticationToken token) {

        Optional<User> user = this.userRepository.findById(token.getName());

        if (user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Envia evento para exclusão de dados em outros serviços
        this.kafkaTemplate.send(
                "delete-user-topic", new EventDeleteUser(user.get().getUserId()));

        // Remove usuário do banco de dados
        this.userRepository.deleteById(user.get().getUserId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

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

        var user = this.userRepository.findById(token.getName());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "Bad request", "This CPF was not the one used when creating the account. Process denied."
            ));
        }

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

        var user = this.userRepository.findById(token.getName());

        if (user.isEmpty()) {
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
}