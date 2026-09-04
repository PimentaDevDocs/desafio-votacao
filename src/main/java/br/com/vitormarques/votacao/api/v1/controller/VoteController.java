package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.VoteRequest;
import br.com.vitormarques.votacao.api.v1.dto.VoteResponse;
import br.com.vitormarques.votacao.api.v1.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/topics/{topicId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VoteResponse register(@PathVariable Long topicId, @Valid @RequestBody VoteRequest request) {
        return service.register(topicId, request);
    }
}