package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.dto.screen.Screen;
import br.com.vitormarques.votacao.api.v1.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/screens")
@RequiredArgsConstructor
@Validated
@Tag(name = "Screens", description = "Screen definitions consumed by the mobile client (Annex 1)")
public class ScreenController {

    private final ScreenService service;

    @Operation(summary = "Form to create a topic")
    @GetMapping("/topics/new")
    public Screen newTopic() {
        return service.newTopicForm();
    }

    @Operation(summary = "Form to open a topic's voting session")
    @GetMapping("/topics/{topicId}/session")
    public Screen openSession(@PathVariable Long topicId) {
        return service.openSessionForm(topicId);
    }

    @Operation(summary = "Yes/No selection to vote on a topic")
    @GetMapping("/topics/{topicId}/vote")
    public Screen vote(@PathVariable Long topicId, @RequestParam @NotBlank String memberId) {
        return service.voteSelection(topicId, memberId);
    }
}