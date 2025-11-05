package br.com.bank_user.service.admService;

import br.com.bank_user.dtos.block_user.ActiveUserWithCpf;
import br.com.bank_user.dtos.block_user.BlockUserWithCpf;
import br.com.bank_user.dtos.register_user.ResponseUsersDto;
import br.com.bank_user.enums.UserStatus;
import br.com.bank_user.model.User;
import br.com.bank_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço administrativo para gerenciamento de usuários
 * Fornece funcionalidades específicas para administradores do sistema
 *
 * @author Pablo R.
 */
@Service
public class AdmService {

    private final UserRepository userRepository;

    /**
     * Construtor com injeção de dependência do UserRepository
     * @param repository Repositório para operações de persistência de usuários
     */
    @Autowired
    public AdmService(UserRepository repository){
        this.userRepository = repository;
    }

    /**
     * Recupera todos os usuários cadastrados no sistema
     * Converte a lista de entidades User para DTOs de resposta
     *
     * @return ResponseEntity contendo lista de ResponseUsersDto com status 200
     *
     * Dados incluídos na resposta:
     * - ID do usuário
     * - CPF
     * - Nome completo
     * - Email
     * - Telefone
     * - Data de registro
     * - Perfil (Role)
     * - Status de autenticação
     * - Status da conta
     */
    public ResponseEntity<List<ResponseUsersDto>> getAllUsers() {
        // Recupera todos os usuários do banco de dados
        List<User> users = this.userRepository.findAll();

        // Converte lista de entidades User para lista de DTOs de resposta
        List<ResponseUsersDto> response = users.stream()
                .map(user -> new ResponseUsersDto(
                        user.getUserId(),
                        user.getCpf(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getDate().toString(),
                        user.getRole().toString(),
                        user.getAuthenticatedClient().toString(),
                        user.getStatus().toString()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Ativa uma conta de usuário previamente bloqueada ou inativa
     * Busca usuário pelo CPF e altera status para ACTIVE
     *
     * @param request DTO contendo CPF do usuário a ser ativado
     * @return ResponseEntity com status:
     *         - 200 OK se ativação for bem-sucedida
     *         - 404 NOT FOUND se usuário não for encontrado
     *
     * @throws jakarta.transaction.Transactional Garante atomicidade na operação
     */
    @Transactional
    public ResponseEntity<Void> activeUser(ActiveUserWithCpf request) {

        // Busca usuário pelo CPF fornecido
        Optional<User> user = this.userRepository.findByCpf(request.cpf());

        // Verifica se usuário existe
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Altera status do usuário para ATIVO
        user.get().setStatus(UserStatus.ACTIVE);
        this.userRepository.save(user.get());
        return ResponseEntity.ok().build();
    }

    /**
     * Bloqueia uma conta de usuário ativa
     * Busca usuário pelo CPF e altera status para BLOCKED
     *
     * @param request DTO contendo CPF do usuário a ser bloqueado
     * @return ResponseEntity com status:
     *         - 200 OK se bloqueio for bem-sucedido
     *         - 404 NOT FOUND se usuário não for encontrado
     *
     * @throws jakarta.transaction.Transactional Garante atomicidade na operação
     */
    @Transactional
    public ResponseEntity<Void> blockUser(BlockUserWithCpf request) {

        // Busca usuário pelo CPF fornecido
        Optional<User> user = this.userRepository.findByCpf(request.cpf());

        // Verifica se usuário existe
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Altera status do usuário para BLOQUEADO
        user.get().setStatus(UserStatus.BLOCKED);
        this.userRepository.save(user.get());
        return ResponseEntity.ok().build();
    }
}