package br.com.vitormarques.votacao.api.v1.exception;

public class VotingSessionNotFoundException extends NotFoundException {

    public VotingSessionNotFoundException(Long topicId) {
        super("No voting session for topic: " + topicId);
    }
}