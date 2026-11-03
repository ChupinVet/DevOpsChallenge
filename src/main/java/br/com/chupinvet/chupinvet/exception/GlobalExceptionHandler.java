package br.com.chupinvet.chupinvet.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<String> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(DadoDuplicadoException.class)
    public ResponseEntity<String> tratarDadoDuplicado(
            DadoDuplicadoException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> tratarAcessoNegado(
            AccessDeniedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarCamposInvalidos(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> erros = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage())
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erros);
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> tratarAutenticacaoInvalida(
            AuthenticationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("E-mail ou senha inválidos");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarErroGenerico(
            Exception exception
    ) {
        logger.error("Erro não tratado", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno no servidor");
    }
}