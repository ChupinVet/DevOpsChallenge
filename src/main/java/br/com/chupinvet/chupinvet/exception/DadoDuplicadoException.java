package br.com.chupinvet.chupinvet.exception;

public class DadoDuplicadoException extends RuntimeException {

    public DadoDuplicadoException(String mensagem) {
        super(mensagem);
    }
}