package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.CreateTopicRequest;
import br.com.vitormarques.votacao.api.v1.dto.TopicResponse;
import br.com.vitormarques.votacao.api.v1.service.TopicService;
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
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService service;

    @PostMapping
    public ResponseEntity<TopicResponse> create(@Valid @RequestBody CreateTopicRequest request) {
        var topic = service.create(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(topic.id())
                .toUri();
        return ResponseEntity.created(location).body(topic);
    }

    @GetMapping("/{id}")
    public TopicResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}