package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.VoteRequest;
import br.com.vitormarques.votacao.api.v1.dto.VoteResponse;
import br.com.vitormarques.votacao.api.v1.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/votes")
@RequiredArgsConstructor
@Tag(name = "Votes", description = "Member vote registration")
public class VoteController {

    private final VoteService service;

    @Operation(summary = "Register a member's vote", description = "One vote per member per topic. Requires an open session.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoteResponse register(@PathVariable Long topicId, @Valid @RequestBody VoteRequest request) {
        return service.register(topicId, request);
    }
}