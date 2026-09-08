package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.VotingResultResponse;
import br.com.vitormarques.votacao.service.VotingResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/result")
@RequiredArgsConstructor
@Tag(name = "Results", description = "Vote counting")
public class VotingResultController {

    private final VotingResultService service;

    @Operation(summary = "Voting result for the topic", description = "Partial count while open; APPROVED/REJECTED/TIED once closed.")
    @GetMapping
    public VotingResultResponse byTopic(@PathVariable Long topicId) {
        return service.byTopic(topicId);
    }
}