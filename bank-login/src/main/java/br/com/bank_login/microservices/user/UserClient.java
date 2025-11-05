package br.com.bank_login.microservices.user;

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
     * Busca o CPF de um usuário pelo seu identificador
     *
     * @param cpf CPF do usuário para consulta
     * @return String contendo o CPF encontrado
     */
    @GetMapping("/microservice/bank_user/cpf")
    String findByCpf(@RequestParam String cpf);

    /**
     * Verifica se a senha fornecida corresponde à senha criptografada do usuário
     *
     * @param cpf CPF do usuário para verificação
     * @param password Senha em texto puro para verificação
     * @return boolean true se a senha corresponder, false caso contrário
     */
    @GetMapping("/microservice/bank_user/password")
    boolean verifyPasswordEncode(@RequestParam String cpf, @RequestParam String password);

    /**
     * Busca o ID único do usuário pelo CPF
     *
     * @param cpf CPF do usuário para busca
     * @return String contendo o ID único do usuário
     */
    @GetMapping("/microservice/bank_user/id")
    String findByIdWithCpf(@RequestParam String cpf);

    /**
     * Busca a role (perfil) do usuário pelo CPF
     *
     * @param cpf CPF do usuário para consulta
     * @return String contendo a role do usuário (ex: USER, ADMIN)
     */
    @GetMapping("/microservice/bank_user/role")
    String findByRoleWithCpf(@RequestParam String cpf);

    /**
     * Busca o status de autenticação do usuário pelo CPF
     *
     * @param cpf CPF do usuário para consulta
     * @return String contendo o status do usuário
     */
    @GetMapping("/microservice/bank_user/status")
    String findByStatusWithCpf(@RequestParam String cpf);

    /**
     * Verifica se o email do usuário foi verificado
     *
     * @param cpf CPF do usuário para verificação
     * @return boolean true se o email foi verificado, false caso contrário
     */
    @GetMapping("/microservice/bank_user/verify")
    boolean verifyEmailWithCpf(@RequestParam String cpf);

    /**
     * Busca o CPF do usuário pelo seu ID único
     *
     * @param userId ID único do usuário para busca
     * @return String contendo o CPF do usuário
     */
    @GetMapping("/microservice/bank_user/refresh")
    String findCpfWithId(@RequestParam String userId);
}