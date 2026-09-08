package br.com.vitormarques.votacao.entity;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VotingSessionTest {

    private final Topic topic = new Topic("Qualquer", null);
    private final LocalDateTime openedAt = LocalDateTime.of(2026, 9, 4, 10, 0);

    @Test
    void shouldBeOpenBeforeClosingTime() {
        var session = new VotingSession(topic, openedAt, Duration.ofMinutes(1));

        assertThat(session.isOpenAt(openedAt.plusSeconds(59))).isTrue();
    }

    @Test
    void shouldBeClosedAtClosingTime() {
        var session = new VotingSession(topic, openedAt, Duration.ofMinutes(1));

        assertThat(session.isOpenAt(openedAt.plusMinutes(1))).isFalse();
    }
}