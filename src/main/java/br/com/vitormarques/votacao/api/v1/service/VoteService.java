package br.com.vitormarques.votacao.api.v1.service;

import br.com.vitormarques.votacao.api.v1.client.EligibilityClient;
import br.com.vitormarques.votacao.api.v1.dto.VoteRequest;
import br.com.vitormarques.votacao.api.v1.dto.VoteResponse;
import br.com.vitormarques.votacao.api.v1.entity.Vote;
import br.com.vitormarques.votacao.api.v1.enums.EligibilityStatus;
import br.com.vitormarques.votacao.api.v1.exception.MemberAlreadyVotedException;
import br.com.vitormarques.votacao.api.v1.exception.MemberUnableToVoteException;
import br.com.vitormarques.votacao.api.v1.exception.VotingSessionClosedException;
import br.com.vitormarques.votacao.api.v1.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.vitormarques.votacao.api.v1.client.FakeEligibilityClient.mask;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionService sessionService;
    private final EligibilityClient eligibilityClient;

    @Transactional
    public VoteResponse register(Long topicId, VoteRequest request) {
        var session = sessionService.requireByTopicId(topicId);
        var cpf = request.memberId();

        if (!session.isOpen()) {
            log.warn("Vote rejected, session closed topicId={} memberId={}", topicId, mask(cpf));
            throw new VotingSessionClosedException(topicId);
        }

        if (eligibilityClient.check(cpf) == EligibilityStatus.UNABLE_TO_VOTE) {
            log.warn("Vote rejected, member unable to vote topicId={} memberId={}", topicId, mask(cpf));
            throw new MemberUnableToVoteException(cpf);
        }

        try {
            var vote = voteRepository.saveAndFlush(new Vote(session, cpf, request.choice()));
            log.info("Vote registered topicId={} memberId={} choice={}", topicId, mask(cpf), request.choice());
            return VoteResponse.from(vote);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Vote rejected, member already voted topicId={} memberId={}", topicId, mask(cpf));
            throw new MemberAlreadyVotedException(topicId, cpf);
        }
    }
}