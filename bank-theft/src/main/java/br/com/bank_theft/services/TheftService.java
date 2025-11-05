package br.com.bank_theft.services;

import br.com.bank_theft.dtos.RequestTheftDto;
import br.com.bank_theft.dtos.ResponseReports;
import br.com.bank_theft.enums.StatusOfReport;
import br.com.bank_theft.models.Theft;
import br.com.bank_theft.repositories.TheftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de relatos de roubo e furto no sistema bancário
 * Responsável por operações de registro e consulta de incidentes de segurança
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 * @author Pablo R.
 */
@Service
public class TheftService {

    private final TheftRepository theftRepository;

    /**
     * Construtor para injeção de dependência do TheftRepository
     * @param repository Repositório para operações de banco de dados de roubos
     */
    @Autowired
    public TheftService(TheftRepository repository) {
        this.theftRepository = repository;
    }

    /**
     * Registra um novo relato de roubo ou furto no sistema
     *
     * @param request DTO contendo dados do incidente reportado
     * @return ResponseEntity com mensagem de confirmação do registro
     * @throws RuntimeException Em caso de erro durante o processamento do relato
     * @throws jakarta.transaction.Transactional Garante atomicidade na operação
     *
     * @implNote Fluxo de registro:
     * 1. Cria nova entidade Theft a partir do DTO
     * 2. Define status inicial como PENDING
     * 3. Persiste no banco de dados
     * 4. Retorna confirmação de sucesso
     *
     * @security O status inicial é sempre PENDING, requerendo análise posterior
     */
    @Transactional
    public ResponseEntity<Map<String, String>> reportTheft(RequestTheftDto request){

        try {
            var theft = new Theft();
            theft.setDateOfTheft(request.dateOfTheft());
            theft.setTimeOfTheft(request.timeOfTheft());
            theft.setLocationOfTheft(request.locationOfTheft());
            theft.setTransactionId(request.transactionId());
            theft.setAmountLost(request.amountLost());
            theft.setDescription(request.description());
            theft.setStatus(StatusOfReport.PENDING);
            this.theftRepository.save(theft);

            return ResponseEntity.ok(Map.of(
                    "message", "Theft reported successfully"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Busca todos os relatos de roubo registrados no sistema
     *
     * @return ResponseEntity com lista de DTOs contendo todos os relatos
     * @apiNote Utilizado para painel administrativo de análise de incidentes
     * @security Acesso tipicamente restrito a administradores e equipe de segurança
     *
     * @implNote Converte entidades Theft para DTOs ResponseReports para evitar
     * exposição de dados sensíveis e estruturar a resposta de forma controlada
     */
    public ResponseEntity<List<ResponseReports>> findAllReports() {
        List<Theft> reports = this.theftRepository.findAll();

        List<ResponseReports> response = reports.stream()
                .map(report -> new ResponseReports(
                        report.getDateOfTheft().toString(),
                        report.getTimeOfTheft().toString(),
                        report.getLocationOfTheft(),
                        report.getTransactionId(),
                        report.getAmountLost(),
                        report.getDescription(),
                        report.getTimestampOfTheft().toString(),
                        report.getStatus().name()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}