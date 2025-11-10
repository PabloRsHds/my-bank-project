package br.com.bank_login.microservices.user;

import br.com.bank_login.dtos.user.ResponseUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Cliente Feign para comunicação com o microserviço de usuários
 * Realiza chamadas HTTP para operações de consulta e verificação de usuários
 *
 * @feignClient Indica que esta interface é um cliente Feign para o serviço BANK-USER
 *
 * @author Pablo R.
 */
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

    /**
     * Endpoint para buscar usuário pelo CPF
     * Retorna dados completos do usuário baseado no Cadastro de Pessoa Física
     *
     * @param cpf CPF do usuário no formato 123.456.789-00
     * @return ResponseUser com dados completos do usuário ou null se não encontrado
     *
     * @apiNote Utilizado para validação por documento fiscal entre microserviços
     * @example
     * GET /microservice/bank_user/get-user-with-cpf?cpf=123.456.789-00
     */
    @GetMapping("/microservice/bank_user/get-user-with-cpf")
    ResponseUser findUserWithCpf(@RequestParam String cpf);
}