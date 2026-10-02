package br.com.socialconnect.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiProblemDetail> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<ApiProblemDetail.FieldError> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiProblemDetail.FieldError(
                        error.getField(), error.getDefaultMessage()))
                .toList();

        boolean estoqueNegativo = exception.getBindingResult().getFieldErrors().stream()
                .anyMatch(error -> "EstoqueNaoNegativo".equals(error.getCode()));
        HttpStatus status = estoqueNegativo ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.BAD_REQUEST;
        return response(status,
                estoqueNegativo ? "Estoque inválido" : "Erro de validação",
                estoqueNegativo ? "Os valores de estoque não podem ser negativos." : "Um ou mais campos são inválidos.",
                request,
                estoqueNegativo ? "https://socialconnect.api/errors/estoque-negativo" : "https://socialconnect.api/errors/validacao",
                errors);
    }

    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ApiProblemDetail> handleCpfDuplicado(
            CpfDuplicadoException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT,
                "CPF já cadastrado",
                exception.getMessage(),
                request,
                "https://socialconnect.api/errors/cpf-duplicado",
                List.of());
    }

    @ExceptionHandler(NomeProdutoDuplicadoException.class)
    public ResponseEntity<ApiProblemDetail> handleNomeProdutoDuplicado(
            NomeProdutoDuplicadoException exception, HttpServletRequest request) {
        return response(HttpStatus.CONFLICT,
                "Produto já cadastrado",
                exception.getMessage(),
                request,
                "https://socialconnect.api/errors/nome-duplicado",
                List.of());
    }

    @ExceptionHandler(EstoqueNegativoException.class)
    public ResponseEntity<ApiProblemDetail> handleEstoqueNegativo(
            EstoqueNegativoException exception, HttpServletRequest request) {
        return response(HttpStatus.UNPROCESSABLE_ENTITY,
                "Estoque inválido",
                exception.getMessage(),
                request,
                "https://socialconnect.api/errors/estoque-negativo",
                List.of());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiProblemDetail> handleNaoEncontrado(
            RecursoNaoEncontradoException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage(),
                request,
                "https://socialconnect.api/errors/nao-encontrado",
                List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiProblemDetail> handleStatus(
            ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return response(status,
                status.getReasonPhrase(),
                exception.getReason(),
                request,
                "https://socialconnect.api/errors/http",
                List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiProblemDetail> handleGenerico(
            Exception exception, HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor",
                "Ocorreu um erro inesperado. Tente novamente.",
                request,
                "https://socialconnect.api/errors/erro-interno",
                List.of());
    }

    private ResponseEntity<ApiProblemDetail> response(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request,
            String type,
            List<ApiProblemDetail.FieldError> errors) {
        ApiProblemDetail problem = new ApiProblemDetail(
                type,
                title,
                status.value(),
                detail,
                request.getRequestURI(),
                LocalDateTime.now(),
                errors);
        return ResponseEntity.status(status).body(problem);
    }
}
