package br.com.bank_wallet.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente Feign para comunicação com o microserviço de usuários
 * Realiza chamadas HTTP para operações de consulta e identificação de usuários
 *
 * @feignClient Indica que esta interface é um cliente Feign para o serviço BANK-USER
 *
 * @author Pablo R.
 */
@FeignClient(name = "BANK-USER")
public interface UserClient {

    /**
     * Busca o ID único do usuário pelo CPF
     *
     * @param cpf CPF do usuário para busca
     * @return String contendo o ID único do usuário
     */
    @GetMapping("/microservice/bank_user/id")
    String findByIdWithCpf(@RequestParam String cpf);

    /**
     * Busca o ID único do usuário pelo número de telefone
     *
     * @param phone Número de telefone do usuário para busca
     * @return String contendo o ID único do usuário
     */
    @GetMapping("/microservice/bank_user/phone-id")
    String findByIdWithPhone(@RequestParam String phone);

    /**
     * Busca o ID único do usuário pelo endereço de email
     *
     * @param email Endereço de email do usuário para busca
     * @return String contendo o ID único do usuário
     */
    @GetMapping("/microservice/bank_user/email-id")
    String findByIdWithEmail(@RequestParam String email);

    /**
     * Busca o nome completo do usuário pelo ID único
     *
     * @param userId ID único do usuário para busca
     * @return String contendo o nome completo do usuário
     */
    @GetMapping("/microservice/bank_user/full-name")
    String findByNameWithId(@RequestParam String userId);
}