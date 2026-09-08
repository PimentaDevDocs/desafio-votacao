package br.com.vitormarques.votacao.entity;

import br.com.vitormarques.votacao.enums.VotingSessionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "voting_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VotingSession extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false, unique = true)
    private Topic topic;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closes_at", nullable = false)
    private LocalDateTime closesAt;

    public VotingSession(Topic topic, LocalDateTime openedAt, Duration duration) {
        this.topic = topic;
        this.openedAt = openedAt;
        this.closesAt = openedAt.plus(duration);
    }

    public boolean isOpen() {
        return isOpenAt(LocalDateTime.now());
    }

    public boolean isOpenAt(LocalDateTime moment) {
        return moment.isBefore(closesAt);
    }

    public VotingSessionStatus status() {
        return isOpen() ? VotingSessionStatus.OPEN : VotingSessionStatus.CLOSED;
    }
}