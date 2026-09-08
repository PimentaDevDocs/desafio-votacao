package br.com.vitormarques.votacao.repository;

import br.com.vitormarques.votacao.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}