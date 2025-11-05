package br.com.bank_document.consumer;

import br.com.bank_document.dtos.ConsumerCardAnalysis;
import br.com.bank_document.dtos.ConsumerCreditDocuments;
import br.com.bank_document.dtos.ConsumerDeleteUser;
import br.com.bank_document.enums.Status;
import br.com.bank_document.models.CreditDocument;
import br.com.bank_document.models.Document;
import br.com.bank_document.repositories.CreditDocumentRepository;
import br.com.bank_document.repositories.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * Serviço consumidor de mensagens Kafka para processamento de documentos e exclusão de usuários.
 * Responsável por ouvir eventos de análise de documentos, documentos de crédito e exclusão de usuários.
 *
 * @author Pablo R.
 */
@Service
public class DocumentConsumer {

    private final DocumentRepository documentRepository;
    private final CreditDocumentRepository creditDocumentRepository;

    /**
     * Construtor para injeção de dependências dos repositórios.
     * @param repository Repositório de documentos cadastrais
     * @param repository1 Repositório de documentos de crédito
     */
    @Autowired
    public DocumentConsumer(
            DocumentRepository repository,
            CreditDocumentRepository repository1){
        this.documentRepository = repository;
        this.creditDocumentRepository = repository1;
    }

    /**
     * Consumidor para o tópico de análise de documentos cadastrais.
     * Registra um novo documento cadastral com status PENDING, se não existir para o usuário.
     *
     * @param consumer DTO contendo os dados do documento cadastral
     * @param ack Objeto para confirmação manual do offset da mensagem
     * @kafkaListener Configurado para o tópico "documents-analysis-topic" com groupId "documents-analysis-group"
     * @throws RuntimeException Em caso de erro durante o processamento
     */
    @KafkaListener( topics = "documents-analysis-topic",
            groupId = "documents-analysis-group",
            containerFactory = "kafkaListenerCardAnalysisFactory")
    public void registerDocument(ConsumerCardAnalysis consumer, Acknowledgment ack){

        try {

            var documents = this.documentRepository
                    .findByUserId(consumer.userId());

            if (documents.isPresent()) {
                return;
            }

            var document = new Document();
            document.setUserId(consumer.userId());
            document.setFullName(consumer.fullName());
            document.setRg(consumer.rg());
            document.setCpf(consumer.cpf());
            document.setAddressFile(consumer.proofOfAddress());
            document.setIncomeFile(consumer.proofOfIncome());
            document.setStatus(Status.PENDING);
            this.documentRepository.save(document);
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Consumidor para o tópico de análise de documentos de crédito.
     * Registra um novo documento de crédito com status PENDING, se não existir para o usuário.
     *
     * @param consumer DTO contendo os dados do documento de crédito
     * @param ack Objeto para confirmação manual do offset da mensagem
     * @kafkaListener Configurado para o tópico "credit-documents-analysis-topic" com groupId "credit-documents-analysis-group"
     * @throws RuntimeException Em caso de erro durante o processamento
     */
    @KafkaListener(topics = "credit-documents-analysis-topic",
            groupId = "credit-documents-analysis-group",
            containerFactory = "kafkaListenerCreditDocumentsFactory")
    public void registerCreditDocument(ConsumerCreditDocuments consumer, Acknowledgment ack){

        try {
            var documents = this.creditDocumentRepository
                    .findByUserId(consumer.userId());

            if (documents.isPresent()) {
                return;
            }

            var document = new CreditDocument();
            document.setUserId(consumer.userId());
            document.setFullName(consumer.fullName());
            document.setCpf(consumer.cpf());
            document.setDate(consumer.date());
            document.setOccupation(consumer.occupation());
            document.setSalary(consumer.salary());
            document.setIncomeFile(consumer.proofOfIncome());
            document.setStatus(Status.PENDING);
            this.creditDocumentRepository.save(document);
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Consumidor para o tópico de exclusão de usuários.
     * Remove todos os documentos cadastrais e de crédito associados ao usuário.
     *
     * @param consumer DTO contendo o ID do usuário a ser excluído
     * @param ack Objeto para confirmação manual do offset da mensagem
     * @kafkaListener Configurado para o tópico "delete-user-topic" com groupId "delete-user-group2"
     * @transactional Garante que a operação de exclusão seja atômica
     */
    @KafkaListener(topics = "delete-user-topic",
            groupId = "delete-user-group2",
            containerFactory = "kafkaListenerDeleteUserFactory")
    @Transactional
    public void deleteUserId(ConsumerDeleteUser consumer, Acknowledgment ack){

        this.documentRepository.deleteAllByUserId(consumer.userId());
        this.creditDocumentRepository.deleteAllByUserId(consumer.userId());
        ack.acknowledge();
    }
}