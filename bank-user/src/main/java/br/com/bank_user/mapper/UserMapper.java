package br.com.bank_user.mapper;

import br.com.bank_user.dtos.register_user.RequestUserDto;
import br.com.bank_user.model.User;
import br.com.bank_user.enums.Role;
import br.com.bank_user.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper responsável pela conversão entre DTOs e entidades de usuário
 * Centraliza a lógica de transformação de dados para o domínio de usuários
 *
 * @component Indica que esta classe é um componente Spring gerenciado pelo container
 * @author Pablo R.
 */
@Component
public class UserMapper {

    /**
     * Converte um RequestUserDto para a entidade User com dados adicionais de segurança
     *
     * @param request DTO com dados básicos de registro do usuário
     * @param passwordEncode Senha já criptografada para armazenamento seguro
     * @param code Código de verificação de email gerado aleatoriamente
     * @param expireCode Data e hora de expiração do código de verificação
     * @return Entidade User populada com todos os dados necessários para persistência
     *
     * @implSpec Este método define valores padrão para:
     * - Role: USER (perfil padrão para novos usuários)
     * - Status: ACTIVE (conta ativa por padrão)
     * - Campos de verificação: code e expireCode fornecidos como parâmetro
     *
     * @example
     * User user = userMapper.toEntity(
     *     requestDto,
     *     "hashedPassword123",
     *     "123456",
     *     LocalDateTime.now().plusMinutes(15)
     * );
     */
    public User toEntity(RequestUserDto request, String passwordEncode, String code, LocalDateTime expireCode){
        var user = new User();
        user.setCpf(request.cpf());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncode);
        user.setPhone(request.phone());
        user.setDate(request.date());
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCode(code);
        user.setExpireCode(expireCode);
        return user;
    }
}
