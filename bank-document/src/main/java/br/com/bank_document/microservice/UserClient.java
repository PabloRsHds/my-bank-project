package br.com.bank_document.microservice;

import br.com.bank_document.dtos.user.ResponseUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "BANK-USER")
public interface UserClient {

    /**
     * Endpoint para buscar usuário pelo ID único do sistema
     * Retorna dados completos do usuário baseado no identificador interno
     *
     * @param userId ID único do usuário (UUID) gerado pelo sistema
     * @return ResponseUser com dados completos do usuário ou null se não encontrado
     *
     * @apiNote Utilizado para validação interna e recuperação de dados entre microserviços
     * @example
     * GET /microservice/bank_user/get-user-with-id?userId=123e4567-e89b-12d3-a456-426614174000
     */
    @GetMapping("/microservice/bank_user/get-user-with-id")
    ResponseUser findUserWithId(@RequestParam String userId);
}
