package br.com.vitormarques.votacao.api.v1.dto;

import br.com.vitormarques.votacao.api.v1.entity.VotingSession;
import br.com.vitormarques.votacao.api.v1.enums.VotingSessionStatus;

import java.time.LocalDateTime;

public record VotingSessionResponse(
        Long id,
        Long topicId,
        LocalDateTime openedAt,
        LocalDateTime closesAt,
        VotingSessionStatus status
) {

    public static VotingSessionResponse from(VotingSession session) {
        return new VotingSessionResponse(
                session.getId(),
                session.getTopic().getId(),
                session.getOpenedAt(),
                session.getClosesAt(),
                session.status()
        );
    }
}