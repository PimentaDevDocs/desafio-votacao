package br.com.vitormarques.votacao.api.v1.service;

import br.com.vitormarques.votacao.api.v1.dto.OpenVotingSessionRequest;
import br.com.vitormarques.votacao.api.v1.dto.VotingSessionResponse;
import br.com.vitormarques.votacao.api.v1.entity.VotingSession;
import br.com.vitormarques.votacao.api.v1.exception.TopicNotFoundException;
import br.com.vitormarques.votacao.api.v1.exception.VotingSessionAlreadyOpenedException;
import br.com.vitormarques.votacao.api.v1.exception.VotingSessionNotFoundException;
import br.com.vitormarques.votacao.api.v1.repository.TopicRepository;
import br.com.vitormarques.votacao.api.v1.repository.VotingSessionRepository;
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
    private final TopicRepository topicRepository;

    @Transactional
    public VotingSessionResponse open(Long topicId, OpenVotingSessionRequest request) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));

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
        return sessionRepository.findByTopicId(topicId)
                .map(VotingSessionResponse::from)
                .orElseThrow(() -> new VotingSessionNotFoundException(topicId));
    }
}