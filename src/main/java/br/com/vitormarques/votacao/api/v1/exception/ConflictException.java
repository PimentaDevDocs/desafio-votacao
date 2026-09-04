package br.com.vitormarques.votacao.api.v1.exception;

public abstract class ConflictException extends RuntimeException {

    protected ConflictException(String message) {
        super(message);
    }
}