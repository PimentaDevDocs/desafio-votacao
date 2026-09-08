package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.OpenVotingSessionRequest;
import br.com.vitormarques.votacao.api.v1.dto.VotingSessionResponse;
import br.com.vitormarques.votacao.service.VotingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/session")
@RequiredArgsConstructor
@Tag(name = "Voting sessions", description = "Opening and lookup of a topic's voting session")
public class VotingSessionController {

    private final VotingSessionService service;

    @Operation(summary = "Open the topic's voting session", description = "Duration in minutes is optional; defaults to 1 minute.")
    @PostMapping
    public ResponseEntity<VotingSessionResponse> open(
            @PathVariable Long topicId,
            @Valid @RequestBody(required = false) OpenVotingSessionRequest request) {

        var body = request == null ? new OpenVotingSessionRequest(null) : request;
        var session = service.open(topicId, body);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(session);
    }

    @Operation(summary = "Find the topic's voting session")
    @GetMapping
    public VotingSessionResponse find(@PathVariable Long topicId) {
        return service.findByTopicId(topicId);
    }
}