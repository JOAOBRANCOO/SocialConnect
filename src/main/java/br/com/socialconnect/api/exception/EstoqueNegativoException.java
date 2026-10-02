package br.com.socialconnect.api.exception;

public class EstoqueNegativoException extends RuntimeException {

    public EstoqueNegativoException(String message) {
        super(message);
    }

    public EstoqueNegativoException(Integer estoqueAtual) {
        super("O estoque não pode ser negativo. Valor informado: " + estoqueAtual);
    }
}
