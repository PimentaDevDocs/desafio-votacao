package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.enums.VotingResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VotingResultTest {

    @Test
    void shouldApproveWhenYesWins() {
        assertThat(VotingResult.of(3, 2)).isEqualTo(VotingResult.APPROVED);
    }

    @Test
    void shouldRejectWhenNoWins() {
        assertThat(VotingResult.of(1, 4)).isEqualTo(VotingResult.REJECTED);
    }

    @Test
    void shouldTieWhenEqual() {
        assertThat(VotingResult.of(2, 2)).isEqualTo(VotingResult.TIED);
    }

    @Test
    void shouldTieWhenNoVotes() {
        assertThat(VotingResult.of(0, 0)).isEqualTo(VotingResult.TIED);
    }
}