package br.com.vitormarques.votacao.api.v1.exception;

public class VotingSessionClosedException extends BusinessRuleException {

    public VotingSessionClosedException(Long topicId) {
        super("Voting session is closed for topic: " + topicId);
    }
}