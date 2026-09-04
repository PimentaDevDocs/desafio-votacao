package br.com.vitormarques.votacao.api.v1.repository;

import br.com.vitormarques.votacao.api.v1.entity.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    Optional<VotingSession> findByTopicId(Long topicId);

    boolean existsByTopicId(Long topicId);
}