package br.com.vitormarques.votacao.api.v1.entity;

import br.com.vitormarques.votacao.api.v1.enums.VoteChoice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "vote",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vote_session_member",
                columnNames = {"voting_session_id", "member_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_session_id", nullable = false)
    private VotingSession session;

    @Column(name = "member_id", nullable = false, length = 20)
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private VoteChoice choice;

    public Vote(VotingSession session, String memberId, VoteChoice choice) {
        this.session = session;
        this.memberId = memberId;
        this.choice = choice;
    }
}