package br.com.bank_user.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Classe global para tratamento de exceções em controladores REST
 * Intercepta e trata exceções de forma consistente em toda a aplicação
 *
 * @restControllerAdvice Define esta classe como um manipulador global de exceções para controladores REST
 *
 * @author Pablo R.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manipula exceções de validação de argumentos de métodos
     * Trata erros de validação de dados de entrada em requisições
     *
     * @param ex Exceção MethodArgumentNotValidException lançada pela validação do Spring
     * @return ResponseEntity com status HTTP 400 (Bad Request) e mensagem de erro
     *
     * @exceptionHandler Especifica que este método trata MethodArgumentNotValidException
     *
     * @implNote Este método é acionado quando:
     * - Campos obrigatórios não são preenchidos
     * - Dados não seguem as regras de validação (@Valid, @NotNull, @Size, etc.)
     * - Formato de dados está incorreto
     *
     * @example
     * // Quando um campo obrigatório é nulo
     * // Retorno: { "message": "Validation failed for argument..." }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }
}

