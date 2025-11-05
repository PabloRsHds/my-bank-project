package br.com.bank_user.scheduler;

import br.com.bank_user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço responsável pela limpeza automática de usuários não verificados
 * Executa manutenção periódica do banco de dados removendo contas inativas
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class UserCleanupService {

    private final UserRepository userRepository;

    /**
     * Construtor para injeção de dependência do repositório de usuários
     * @param repository Repositório para operações de banco de dados
     */
    public UserCleanupService(UserRepository repository){
        this.userRepository = repository;
    }

    /**
     * Executa limpeza automática de usuários não verificados
     * Remove contas que não foram verificadas dentro do prazo de 12 horas
     *
     * @transactional Garante atomicidade na operação de deleção em lote
     * @scheduled Executa automaticamente a cada 12.000.000 milissegundos (200 minutos)
     *
     * @implSpec O método:
     * 1. Calcula data limite (12 horas atrás)
     * 2. Remove usuários não verificados criados antes desta data
     * 3. Loga conclusão da operação
     *
     * @cronology Fixed delay de 200 minutos (3,33 horas)
     * @security Remove apenas usuários não verificados, preservando dados sensíveis
     *
     * @example
     * Se executado às 14:00, remove usuários não verificados criados antes de 02:00
     */
    @Transactional
    @Scheduled(fixedDelay = 12000000) // 12.000.000 ms = 200 minutos = 3,33 horas
    public void cleanUnverifiedUsers(){
        // Define o limite de tempo: 12 horas atrás a partir do momento atual
        LocalDateTime cutoff = LocalDateTime.now().minusHours(12);

        // Executa deleção em lote de usuários não verificados anteriores ao cutoff
        userRepository.deleteUnverifiedUsersBefore(cutoff);

        // Log de confirmação da execução (em produção, substituir por logger)
        System.out.println("Cleanup of unverified users completed.");
    }
}
