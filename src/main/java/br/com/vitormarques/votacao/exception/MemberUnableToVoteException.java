package br.com.vitormarques.votacao.exception;

public class MemberUnableToVoteException extends NotFoundException {

    public MemberUnableToVoteException(String cpf) {
        super("Member unable to vote: " + cpf);
    }
}