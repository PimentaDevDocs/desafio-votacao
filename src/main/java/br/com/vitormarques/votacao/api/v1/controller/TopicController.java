package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.CreateTopicRequest;
import br.com.vitormarques.votacao.api.v1.dto.TopicResponse;
import br.com.vitormarques.votacao.api.v1.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "Topic registration and lookup")
public class TopicController {

    private final TopicService service;

    @Operation(summary = "Create a topic")
    @PostMapping
    public ResponseEntity<TopicResponse> create(@Valid @RequestBody CreateTopicRequest request) {
        var topic = service.create(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(topic.id())
                .toUri();
        return ResponseEntity.created(location).body(topic);
    }

    @Operation(summary = "Find a topic by id")
    @GetMapping("/{id}")
    public TopicResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}