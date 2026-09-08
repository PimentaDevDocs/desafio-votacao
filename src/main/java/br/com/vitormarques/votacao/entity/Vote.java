package br.com.vitormarques.votacao.entity;

import br.com.vitormarques.votacao.enums.VoteChoice;
import jakarta.persistence.*;
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