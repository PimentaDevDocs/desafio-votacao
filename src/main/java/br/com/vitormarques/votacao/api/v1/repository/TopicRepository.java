package br.com.vitormarques.votacao.api.v1.repository;

import br.com.vitormarques.votacao.api.v1.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}