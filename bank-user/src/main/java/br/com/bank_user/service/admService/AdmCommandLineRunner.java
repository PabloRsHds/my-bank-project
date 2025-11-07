package br.com.bank_user.service.admService;

import br.com.bank_user.enums.Role;
import br.com.bank_user.enums.UserStatus;
import br.com.bank_user.model.User;
import br.com.bank_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/**
 * Classe de configuração que executa durante a inicialização da aplicação
 * Responsável por criar usuários administradores padrão caso não existam
 *
 * @implNote Implementa CommandLineRunner para execução automática ao iniciar o Spring Boot
 * @configuration Indica que esta classe contém definições de beans Spring
 *
 * @author Pablo R.
 */
@Configuration
public class AdmCommandLineRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construtor para injeção de dependências necessárias
     * @param repository Repositório de usuários para operações de banco de dados
     * @param password Encoder de senhas para criptografia segura
     */
    @Autowired
    public AdmCommandLineRunner(UserRepository repository, PasswordEncoder password){
        this.userRepository = repository;
        this.passwordEncoder = password;
    }

    /**
     * Método executado automaticamente durante a inicialização da aplicação
     * Verifica e cria usuários administradores padrão se não existirem
     *
     * @param args Argumentos de linha de comando (não utilizados)
     * @throws Exception Pode lançar exceções durante operações de banco de dados
     *
     * @transactional Garante que as operações de banco sejam atômicas
     *
     * @implSpec Cria dois administradores padrão:
     * 1. Rodrigo dos Santos - Cliente autenticado
     * 2. Pablo dos Santos - Cliente não autenticado
     *
     * @securityNote As senhas são criptografadas antes do armazenamento
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Verifica e cria o primeiro administrador (Rodrigo)
        var adm1 = this.userRepository.findByEmail("pablo@gmail.com");

        adm1.ifPresentOrElse(
                // Callback executado se o administrador já existir
                presente -> System.out.println("Adm On"),
                // Callback executado se o administrador não existir (criação)
                () -> {
                    var userEntity = new User();
                    userEntity.setCpf("259.088.550-49");
                    userEntity.setFullName("Pablo dos Santos");
                    userEntity.setEmail("pablo@gmail.com");
                    userEntity.setPassword(this.passwordEncoder.encode("123456789Rr@"));
                    userEntity.setPhone("21993666541");
                    userEntity.setDate(LocalDate.of(2000,12,11));
                    userEntity.setRole(Role.ADMIN);
                    userEntity.setStatus(UserStatus.ACTIVE);
                    userEntity.setVerifyEmail(true);
                    userEntity.setAuthenticatedClient(true);
                    this.userRepository.save(userEntity);
                }
        );
    }
}