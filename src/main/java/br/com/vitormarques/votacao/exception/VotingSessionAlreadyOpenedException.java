package br.com.vitormarques.votacao.exception;

public class VotingSessionAlreadyOpenedException extends ConflictException {

    public VotingSessionAlreadyOpenedException(Long topicId) {
        super("Voting session already opened for topic: " + topicId);
    }
}