package br.com.vitormarques.votacao.api.v1.exception;

public class InvalidCpfException extends NotFoundException {

    public InvalidCpfException(String cpf) {
        super("CPF not found: " + cpf);
    }
}