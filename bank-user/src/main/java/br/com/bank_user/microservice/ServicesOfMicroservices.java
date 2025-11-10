package br.com.bank_user.microservice;

import br.com.bank_user.dtos.user.ResponseUser;
import br.com.bank_user.model.User;
import br.com.bank_user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Microserviço para consulta e autenticação de usuários
 * Fornece endpoints para comunicação interna entre serviços do sistema bancário
 *
 * @restController Indica que esta classe é um controlador REST
 * @requestMapping Define o caminho base para todos os endpoints de microserviço
 *
 * @author Pablo R.
 */
@RestController
@RequestMapping("/microservice/bank_user")
public class ServicesOfMicroservices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construtor para injeção de dependências do microserviço de usuários
     *
     * @param repository Repositório para operações de banco de dados de usuários
     * @param password Encoder de senhas para validação de credenciais
     */
    public ServicesOfMicroservices(UserRepository repository, PasswordEncoder password) {
        this.userRepository = repository;
        this.passwordEncoder = password;
    }

    /**
     * Endpoint para buscar usuário pelo ID único
     * Retorna dados completos do usuário para integração entre microserviços
     *
     * @param userId ID único do usuário para consulta
     * @return ResponseUser com dados completos do usuário ou null se não encontrado
     */
    @GetMapping("/get-user-with-id")
    public ResponseUser findUserWithId(@RequestParam String userId) {
        Optional<User> user = this.userRepository.findById(userId);

        return user.map(value -> new ResponseUser(
                value.getUserId(),
                value.getCpf(),
                value.getFullName(),
                value.getEmail(),
                value.getPassword(),
                value.getPhone(),
                value.getDate(),
                value.getRole().toString(),
                value.getStatus().toString(),
                value.getAuthenticatedClient(),
                value.getVerifyEmail()
        )).orElse(null);
    }

    /**
     * Endpoint para buscar usuário pelo CPF
     * Utilizado para validação e recuperação de dados por documento fiscal
     *
     * @param cpf Cpf único do usuário para consulta
     * @return ResponseUser com dados completos do usuário ou null se não encontrado
     */
    @GetMapping("/get-user-with-cpf")
    public ResponseUser findUserWithCpf(@RequestParam String cpf) {
        Optional<User> user = this.userRepository.findByCpf(cpf);

        return user.map(value -> new ResponseUser(
                value.getUserId(),
                value.getCpf(),
                value.getFullName(),
                value.getEmail(),
                value.getPassword(),
                value.getPhone(),
                value.getDate(),
                value.getRole().toString(),
                value.getStatus().toString(),
                value.getAuthenticatedClient(),
                value.getVerifyEmail()
        )).orElse(null);
    }

    /**
     * Endpoint para busca flexível de usuário por múltiplos identificadores
     * Aceita ID, CPF, telefone ou email como chave de pesquisa
     *
     * @param key Chave de pesquisa (ID, CPF, telefone ou email)
     * @return ResponseUser com dados completos do usuário ou null se não encontrado
     */
    @GetMapping("/get-user-with-id-cpf-phone-email")
    public ResponseUser findByUserWithCpfOrPhoneOrEmail(@RequestParam String key) {
        Optional<User> user = this.userRepository.findByUserWithIdOrCpfOrPhoneOrEmail(key);

        return user.map(value -> new ResponseUser(
                value.getUserId(),
                value.getCpf(),
                value.getFullName(),
                value.getEmail(),
                value.getPassword(),
                value.getPhone(),
                value.getDate(),
                value.getRole().toString(),
                value.getStatus().toString(),
                value.getAuthenticatedClient(),
                value.getVerifyEmail()
        )).orElse(null);
    }
}