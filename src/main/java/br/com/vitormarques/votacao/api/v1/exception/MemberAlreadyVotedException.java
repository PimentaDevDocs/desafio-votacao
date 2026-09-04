package br.com.vitormarques.votacao.api.v1.exception;

public class MemberAlreadyVotedException extends ConflictException {

    public MemberAlreadyVotedException(Long topicId, String memberId) {
        super("Member " + memberId + " already voted on topic: " + topicId);
    }
}