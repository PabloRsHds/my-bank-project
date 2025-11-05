package br.com.bank_user.microservice.login;

import br.com.bank_user.model.User;
import br.com.bank_user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Microserviço de login e consulta de usuários
 * Fornece endpoints para autenticação e recuperação de informações de usuário
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o caminho base para todos os endpoints
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/microservice/bank_user")
public class LoginMicroservice {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construtor para injeção de dependências
     * @param repository Repositório de usuários para operações de banco
     * @param password Encoder de senhas para validação de credenciais
     */
    public LoginMicroservice(UserRepository repository, PasswordEncoder password) {
        this.userRepository = repository;
        this.passwordEncoder = password;
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
        Optional<User> user = this.userRepository.findById(userId);
        return user.map(User::getFullName).orElse(null);
    }

    /**
     * Recupera o CPF do usuário pelo CPF (validação de existência)
     *
     * @param cpf Número do CPF para consulta
     * @return CPF do usuário se encontrado
     * @throws RuntimeException Se usuário não for encontrado
     * @implNote Serve como validação de existência do CPF no sistema
     */
    @GetMapping("/cpf")
    public String findByCpf(@RequestParam String cpf) {
        Optional<User> user = this.userRepository.findByCpf(cpf);
        return user.map(User::getCpf).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Recupera o ID do usuário pelo CPF
     *
     * @param cpf Número do CPF para consulta
     * @return ID único do usuário (UUID)
     * @throws RuntimeException Se usuário não for encontrado
     * @apiNote Utilizado para mapear CPF para ID em outros serviços
     */
    @GetMapping("/id")
    public String findByIdWithCpf(@RequestParam String cpf) {
        Optional<User> user = this.userRepository.findByCpf(cpf);
        return user.map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Recupera o ID do usuário pelo número de telefone
     *
     * @param phone Número de telefone para consulta
     * @return ID único do usuário (UUID)
     * @throws RuntimeException Se usuário não for encontrado
     * @apiNote Utilizado para recuperação de conta via telefone
     */
    @GetMapping("/phone-id")
    public String findByIdWithPhone(@RequestParam String phone) {
        Optional<User> user = this.userRepository.findByPhone(phone);
        return user.map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Recupera o ID do usuário pelo email
     *
     * @param email Endereço de email para consulta
     * @return ID único do usuário (UUID)
     * @throws RuntimeException Se usuário não for encontrado
     * @apiNote Utilizado para recuperação de conta e processos de login
     */
    @GetMapping("/email-id")
    public String findByIdWithEmail(@RequestParam String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        return user.map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Recupera o perfil de acesso (role) do usuário pelo CPF
     *
     * @param cpf Número do CPF para consulta
     * @return Perfil de acesso do usuário (USER, ADMIN)
     * @throws RuntimeException Se usuário não for encontrado
     * @apiNote Utilizado para controle de acesso e autorização
     */
    @GetMapping("/role")
    public String findByRoleWithCpf(@RequestParam String cpf) {
        Optional<User> user = this.userRepository.findByCpf(cpf);
        return user.map(user1 -> user1.getRole().toString()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Recupera o status da conta do usuário pelo CPF
     *
     * @param cpf Número do CPF para consulta
     * @return Status da conta (ACTIVE, BLOCKED, etc.)
     * @throws RuntimeException Se usuário não for encontrado
     * @apiNote Utilizado para verificar se conta está ativa antes de operações
     */
    @GetMapping("/status")
    public String findByStatusWithCpf(@RequestParam String cpf) {
        Optional<User> user = this.userRepository.findByCpf(cpf);
        return user.map(user1 -> user1.getStatus().toString()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Verifica se o email do usuário foi verificado
     *
     * @param cpf Número do CPF para consulta
     * @return true se email foi verificado, false caso contrário
     * @throws RuntimeException Se usuário não for encontrado
     * @apiNote Utilizado para validar se usuário completou verificação de email
     */
    @GetMapping("/verify")
    public boolean verifyEmailWithCpf(@RequestParam String cpf) {
        Optional<User> user = this.userRepository.findByCpf(cpf);
        return user.map(User::getVerifyEmail).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Valida se a senha fornecida corresponde à senha do usuário
     *
     * @param cpf Número do CPF para consulta
     * @param password Senha em texto puro para validação
     * @return true se senha corresponder, false caso contrário
     * @throws RuntimeException Se usuário não for encontrado
     * @security A senha é comparada usando BCrypt encoder
     * @apiNote Utilizado para processos de autenticação e troca de senha
     */
    @GetMapping("/password")
    public boolean verifyPasswordEncode(@RequestParam String cpf, @RequestParam String password) {
        Optional<User> user = this.userRepository.findByCpf(cpf);
        if (this.passwordEncoder.matches(password, user.get().getPassword())) {
            return true;
        }
        return false;
    }

    /**
     * Recupera o CPF do usuário pelo ID (endpoint de refresh)
     *
     * @param userId ID único do usuário (UUID)
     * @return CPF do usuário
     * @throws RuntimeException Se usuário não for encontrado
     * @apiNote Utilizado para atualizar informações de sessão ou token
     */
    @GetMapping("/refresh")
    public String findCpfWithId(@RequestParam String userId) {
        Optional<User> user = this.userRepository.findById(userId);
        return user.map(User::getCpf).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
