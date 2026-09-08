package br.com.vitormarques.votacao.service;

import br.com.vitormarques.votacao.api.v1.dto.OpenVotingSessionRequest;
import br.com.vitormarques.votacao.api.v1.dto.VotingSessionResponse;
import br.com.vitormarques.votacao.entity.VotingSession;
import br.com.vitormarques.votacao.exception.VotingSessionAlreadyOpenedException;
import br.com.vitormarques.votacao.exception.VotingSessionNotFoundException;
import br.com.vitormarques.votacao.repository.VotingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotingSessionService {

    private final VotingSessionRepository sessionRepository;
    private final TopicService topicService;

    @Transactional
    public VotingSessionResponse open(Long topicId, OpenVotingSessionRequest request) {
        var topic = topicService.requireById(topicId);

        if (sessionRepository.existsByTopicId(topicId)) {
            throw new VotingSessionAlreadyOpenedException(topicId);
        }

        var session = sessionRepository.save(
                new VotingSession(topic, LocalDateTime.now(), request.duration()));

        log.info("Voting session opened topicId={} closesAt={}", topicId, session.getClosesAt());
        return VotingSessionResponse.from(session);
    }

    @Transactional(readOnly = true)
    public VotingSessionResponse findByTopicId(Long topicId) {
        return VotingSessionResponse.from(requireByTopicId(topicId));
    }

    @Transactional(readOnly = true)
    public VotingSession requireByTopicId(Long topicId) {
        return sessionRepository.findByTopicId(topicId)
                .orElseThrow(() -> new VotingSessionNotFoundException(topicId));
    }
}