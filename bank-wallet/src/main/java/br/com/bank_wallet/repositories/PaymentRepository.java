package br.com.bank_wallet.repositories;

import br.com.bank_wallet.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Payment
 * Fornece métodos para consulta e manipulação de registros de transações financeiras
 *
 * @repository Interface de repositório Spring Data JPA
 * @author Pablo R.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Busca todos os pagamentos enviados por um usuário específico
     * Retorna lista completa do histórico de transações de envio do usuário
     *
     * @param userSend ID do usuário remetente para busca
     * @return Lista de pagamentos enviados pelo usuário
     */
    List<Payment> findAllByUserSend(String userSend);

    /**
     * Busca todos os pagamentos recebidos por um usuário específico
     * Retorna lista completa do histórico de transações de recebimento do usuário
     *
     * @param userReceive ID do usuário destinatário para busca
     * @return Lista de pagamentos recebidos pelo usuário
     */
    List<Payment> findAllByUserReceive(String userReceive);
}