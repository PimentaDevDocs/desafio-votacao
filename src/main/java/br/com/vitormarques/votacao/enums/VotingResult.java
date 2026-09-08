package br.com.vitormarques.votacao.enums;

public enum VotingResult {
    APPROVED, REJECTED, TIED;

    public static VotingResult of(long yes, long no) {
        if (yes > no) {
            return APPROVED;
        }
        if (no > yes) {
            return REJECTED;
        }
        return TIED;
    }
}