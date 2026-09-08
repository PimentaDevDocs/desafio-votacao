package br.com.vitormarques.votacao.exception;

public class InvalidCpfException extends NotFoundException {

    public InvalidCpfException(String cpf) {
        super("CPF not found: " + cpf);
    }
}