package br.com.vitormarques.votacao.api.v1.service;

import br.com.vitormarques.votacao.api.v1.dto.VoteRequest;
import br.com.vitormarques.votacao.api.v1.dto.VoteResponse;
import br.com.vitormarques.votacao.api.v1.entity.Vote;
import br.com.vitormarques.votacao.api.v1.exception.MemberAlreadyVotedException;
import br.com.vitormarques.votacao.api.v1.exception.VotingSessionClosedException;
import br.com.vitormarques.votacao.api.v1.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionService sessionService;

    @Transactional
    public VoteResponse register(Long topicId, VoteRequest request) {
        var session = sessionService.requireByTopicId(topicId);

        if (!session.isOpen()) {
            log.warn("Vote rejected, session closed topicId={} memberId={}", topicId, request.memberId());
            throw new VotingSessionClosedException(topicId);
        }

        try {
            var vote = voteRepository.saveAndFlush(new Vote(session, request.memberId(), request.choice()));
            log.info("Vote registered topicId={} memberId={} choice={}", topicId, request.memberId(), request.choice());
            return VoteResponse.from(vote);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Vote rejected, member already voted topicId={} memberId={}", topicId, request.memberId());
            throw new MemberAlreadyVotedException(topicId, request.memberId());
        }
    }
}