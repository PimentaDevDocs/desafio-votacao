package br.com.vitormarques.votacao.exception;

public class TopicNotFoundException extends NotFoundException {

    public TopicNotFoundException(Long id) {
        super("Topic not found: " + id);
    }
}