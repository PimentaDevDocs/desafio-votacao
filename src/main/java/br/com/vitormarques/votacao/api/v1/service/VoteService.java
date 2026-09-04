package br.com.vitormarques.votacao.api.v1.service;

import br.com.vitormarques.votacao.api.v1.dto.VoteRequest;
import br.com.vitormarques.votacao.api.v1.dto.VoteResponse;
import br.com.vitormarques.votacao.api.v1.entity.Vote;
import br.com.vitormarques.votacao.api.v1.exception.VotingSessionClosedException;
import br.com.vitormarques.votacao.api.v1.exception.VotingSessionNotFoundException;
import br.com.vitormarques.votacao.api.v1.repository.VoteRepository;
import br.com.vitormarques.votacao.api.v1.repository.VotingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository sessionRepository;

    @Transactional
    public VoteResponse register(Long topicId, VoteRequest request) {
        var session = sessionRepository.findByTopicId(topicId)
                .orElseThrow(() -> new VotingSessionNotFoundException(topicId));

        if (!session.isOpen()) {
            log.warn("Vote rejected, session closed topicId={} memberId={}", topicId, request.memberId());
            throw new VotingSessionClosedException(topicId);
        }

        var vote = voteRepository.save(new Vote(session, request.memberId(), request.choice()));

        log.info("Vote registered topicId={} memberId={} choice={}", topicId, request.memberId(), request.choice());
        return VoteResponse.from(vote);
    }
}