package br.com.vitormarques.votacao.exception;

public class VotingSessionClosedException extends BusinessRuleException {

    public VotingSessionClosedException(Long topicId) {
        super("Voting session is closed for topic: " + topicId);
    }
}