package br.com.bank_card.service;

import br.com.bank_card.dtos.ResponseUserCard;
import br.com.bank_card.enums.Status;
import br.com.bank_card.model.Card;
import br.com.bank_card.repository.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço principal para gerenciamento de cartões bancários
 * Responsável por operações de consulta, bloqueio e verificação de cartões
 *
 * @service Indica que esta classe é um serviço Spring gerenciado pelo container
 *
 * @author Pablo R.
 */
@Service
public class CardService {

    private final CardRepository cardRepository;

    /**
     * Construtor para injeção de dependências do repositório de cartões
     *
     * @param cardRepository Repositório para operações de banco de dados de cartões
     */
    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Verifica se o usuário possui um cartão e retorna seu status atual
     * Utilizado para determinar o estado do cartão na interface do usuário
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com string representando o status do cartão:
     *         "EMPTY", "APPROVED", "CANCELED", "BLOCKED" ou "UNAUTHORIZED"
     */
    public ResponseEntity<String> verifyIfUserHasCardAndYourStatus(JwtAuthenticationToken token) {

        Optional<Card> card = this.cardRepository.findByUserId(token.getName());

        if (card.isEmpty()){
            return ResponseEntity.ok().body("EMPTY");
        } else if (card.get().getStatus().equals(Status.APPROVED)) {
            return ResponseEntity.ok().body("APPROVED");
        } else if (card.get().getStatus().equals(Status.CANCELED)) {
            return ResponseEntity.ok().body("CANCELED");
        } else if (card.get().getStatus().equals(Status.BLOCKED)) {
            return ResponseEntity.ok().body("BLOCKED");
        }
        return ResponseEntity.badRequest().body("UNAUTHORIZED");
    }

    /**
     * Recupera os dados completos do cartão do usuário autenticado
     * Retorna informações sensíveis como número do cartão e CVV
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com DTO contendo dados completos do cartão
     */
    public ResponseEntity<ResponseUserCard> getUserCard(JwtAuthenticationToken token) {

        Optional<Card> card = this.cardRepository.findByUserId(token.getName());

        if (card.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return card.map(value -> ResponseEntity.ok().body(new ResponseUserCard(
                value.getFullName(),
                value.getCardNumber(),
                value.getExpirationDate(),
                value.getCardCvv(),
                value.getLimitCredit(),
                value.getTypeOfCard()
        ))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    /**
     * Alterna o status de bloqueio do cartão do usuário
     * Se estiver aprovado, bloqueia; se estiver bloqueado, aprova
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     */
    @Transactional
    public void blockCard(JwtAuthenticationToken token) {

        Optional<Card> card = this.cardRepository
                .findByUserId(token.getName());

        if (card.isEmpty()) {
            return;
        }

        if (card.get().getStatus().equals(Status.APPROVED)) {
            card.get().setStatus(Status.BLOCKED);
            this.cardRepository.save(card.get());

        } else if (card.get().getStatus().equals(Status.BLOCKED)) {
            card.get().setStatus(Status.APPROVED);
            this.cardRepository.save(card.get());
        }
    }

    /**
     * Recupera o limite de crédito disponível no cartão do usuário
     * Retorna null se o usuário não tiver cartão ou limite definido
     *
     * @param token Token JWT de autenticação contendo ID do usuário
     * @return ResponseEntity com o valor do limite de crédito ou null
     */
    public ResponseEntity<Double> getLimitOfCredit(JwtAuthenticationToken token) {

        Optional<Card> card = this.cardRepository.findByUserId(token.getName());

        if (card.isEmpty()) {
            return null;
        } else if (card.isPresent() && card.get().getLimitCredit() == null) {
            return null;
        }

        return card.map(getCard -> ResponseEntity.ok(getCard.getLimitCredit()))
                .orElseThrow();
    }
}