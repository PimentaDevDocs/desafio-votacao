package br.com.vitormarques.votacao.api.v1.exception;

public class MemberUnableToVoteException extends NotFoundException {

    public MemberUnableToVoteException(String cpf) {
        super("Member unable to vote: " + cpf);
    }
}