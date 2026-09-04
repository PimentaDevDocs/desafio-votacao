package br.com.vitormarques.votacao.api.v1.repository;

import br.com.vitormarques.votacao.api.v1.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}