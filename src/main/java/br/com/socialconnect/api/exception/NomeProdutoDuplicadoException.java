package br.com.socialconnect.api.exception;

public class NomeProdutoDuplicadoException extends RuntimeException {

    public NomeProdutoDuplicadoException(String nome) {
        super("Já existe um produto cadastrado com o nome: " + nome);
    }
}
