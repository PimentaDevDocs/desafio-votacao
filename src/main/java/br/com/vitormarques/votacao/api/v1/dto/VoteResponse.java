package br.com.vitormarques.votacao.api.v1.dto;

import br.com.vitormarques.votacao.entity.Vote;
import br.com.vitormarques.votacao.enums.VoteChoice;

import java.time.LocalDateTime;

public record VoteResponse(
        Long id,
        Long topicId,
        String memberId,
        VoteChoice choice,
        LocalDateTime createdAt
) {

    public static VoteResponse from(Vote vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getSession().getTopic().getId(),
                vote.getMemberId(),
                vote.getChoice(),
                vote.getCreatedAt()
        );
    }
}