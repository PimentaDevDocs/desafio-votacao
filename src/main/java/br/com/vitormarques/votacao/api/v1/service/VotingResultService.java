package br.com.vitormarques.votacao.api.v1.service;

import br.com.vitormarques.votacao.api.v1.dto.VotingResultResponse;
import br.com.vitormarques.votacao.api.v1.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotingResultService {

    private final VotingSessionService sessionService;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public VotingResultResponse byTopic(Long topicId) {
        var session = sessionService.requireByTopicId(topicId);
        var count = voteRepository.countBySession(session.getId());
        var response = VotingResultResponse.of(topicId, session.status(), count);

        log.info("Result computed topicId={} status={} yes={} no={}",
                topicId, response.sessionStatus(), response.yes(), response.no());
        return response;
    }
}