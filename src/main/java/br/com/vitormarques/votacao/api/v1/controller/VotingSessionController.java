package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.OpenVotingSessionRequest;
import br.com.vitormarques.votacao.api.v1.dto.VotingSessionResponse;
import br.com.vitormarques.votacao.api.v1.service.VotingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/session")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService service;

    @PostMapping
    public ResponseEntity<VotingSessionResponse> open(
            @PathVariable Long topicId,
            @Valid @RequestBody(required = false) OpenVotingSessionRequest request) {

        var body = request == null ? new OpenVotingSessionRequest(null) : request;
        var session = service.open(topicId, body);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(session);
    }

    @GetMapping
    public VotingSessionResponse find(@PathVariable Long topicId) {
        return service.findByTopicId(topicId);
    }
}