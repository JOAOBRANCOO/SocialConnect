package br.com.socialconnect.api.exception;

public class CpfDuplicadoException extends RuntimeException {

    public CpfDuplicadoException(String cpf) {
        super("O CPF " + cpf + " já está cadastrado no sistema.");
    }
}