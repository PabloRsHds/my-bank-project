package br.com.bank_wallet.feign;

import br.com.bank_wallet.dtos.user.ResponseUser;
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
     * Endpoint para busca flexível de usuário por múltiplos identificadores
     * Permite localizar usuários usando ID, CPF, telefone ou email de forma unificada
     *
     * @param key Chave de pesquisa que pode ser:
     *           - ID único do usuário (UUID)
     *           - CPF no formato 123.456.789-00
     *           - Número de telefone com DDD
     *           - Endereço de email (não diferencia maiúsculas/minúsculas)
     * @return ResponseUser com dados completos do usuário ou null se não encontrado
     */
    @GetMapping("/microservice/bank_user/get-user-with-id-cpf-phone-email")
    ResponseUser findByUserWithCpfOrPhoneOrEmail(@RequestParam String key);
}