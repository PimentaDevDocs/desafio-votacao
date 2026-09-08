package br.com.vitormarques.votacao.repository;

import br.com.vitormarques.votacao.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("""
            select coalesce(sum(case when v.choice = br.com.vitormarques.votacao.enums.VoteChoice.YES then 1 else 0 end), 0) as yes,
                   coalesce(sum(case when v.choice = br.com.vitormarques.votacao.enums.VoteChoice.NO then 1 else 0 end), 0) as no
            from Vote v
            where v.session.id = :sessionId
            """)
    VoteCount countBySession(@Param("sessionId") Long sessionId);
}