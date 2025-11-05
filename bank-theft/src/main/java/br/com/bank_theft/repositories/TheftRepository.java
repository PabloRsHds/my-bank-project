package br.com.bank_theft.repositories;

import br.com.bank_theft.models.Theft;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações de persistência e consulta de entidades Theft
 * Fornece operações CRUD básicas para gerenciamento de registros de roubo e furto
 *
 * @author Pablo R.
 */
public interface TheftRepository extends JpaRepository<Theft, Long> {
}
