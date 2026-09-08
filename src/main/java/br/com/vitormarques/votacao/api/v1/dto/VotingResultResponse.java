package br.com.vitormarques.votacao.api.v1.dto;

import br.com.vitormarques.votacao.enums.VotingResult;
import br.com.vitormarques.votacao.enums.VotingSessionStatus;
import br.com.vitormarques.votacao.repository.VoteCount;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VotingResultResponse(
        Long topicId,
        VotingSessionStatus sessionStatus,
        long yes,
        long no,
        long total,
        VotingResult result
) {

    public static VotingResultResponse of(Long topicId, VotingSessionStatus status, VoteCount count) {
        long yes = count.getYes();
        long no = count.getNo();
        var result = status == VotingSessionStatus.CLOSED ? VotingResult.of(yes, no) : null;
        return new VotingResultResponse(topicId, status, yes, no, yes + no, result);
    }
}