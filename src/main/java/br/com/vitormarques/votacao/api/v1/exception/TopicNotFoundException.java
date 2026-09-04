package br.com.vitormarques.votacao.api.v1.exception;

public class TopicNotFoundException extends NotFoundException {

    public TopicNotFoundException(Long id) {
        super("Topic not found: " + id);
    }
}