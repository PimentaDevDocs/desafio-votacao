package br.com.vitormarques.votacao.api.v1.exception;

public class VotingSessionAlreadyOpenedException extends ConflictException {

    public VotingSessionAlreadyOpenedException(Long topicId) {
        super("Voting session already opened for topic: " + topicId);
    }
}