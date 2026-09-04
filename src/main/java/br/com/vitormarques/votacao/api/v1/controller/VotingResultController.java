package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.VotingResultResponse;
import br.com.vitormarques.votacao.api.v1.service.VotingResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/result")
@RequiredArgsConstructor
public class VotingResultController {

    private final VotingResultService service;

    @GetMapping
    public VotingResultResponse byTopic(@PathVariable Long topicId) {
        return service.byTopic(topicId);
    }
}