package br.com.vitormarques.votacao.service;

import br.com.vitormarques.votacao.api.v1.dto.screen.Button;
import br.com.vitormarques.votacao.api.v1.dto.screen.FormField;
import br.com.vitormarques.votacao.api.v1.dto.screen.Screen;
import br.com.vitormarques.votacao.api.v1.dto.screen.SelectionItem;
import br.com.vitormarques.votacao.config.AppProperties;
import br.com.vitormarques.votacao.enums.VoteChoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final TopicService topicService;
    private final AppProperties properties;

    public Screen newTopicForm() {
        var fields = List.of(
                FormField.text("title", "Título"),
                FormField.text("description", "Descrição")
        );
        var save = new Button("Cadastrar", url("/api/v1/topics"), Map.of());
        return Screen.form("Nova pauta", fields, save);
    }

    @Transactional(readOnly = true)
    public Screen openSessionForm(Long topicId) {
        var topic = topicService.requireById(topicId);
        var fields = List.of(
                FormField.label(topic.getTitle()),
                FormField.number("durationMinutes", "Duração em minutos (padrão: 1)")
        );
        var open = new Button("Abrir sessão", url("/api/v1/topics/" + topicId + "/session"), Map.of());
        return Screen.form("Abrir sessão de votação", fields, open);
    }

    @Transactional(readOnly = true)
    public Screen voteSelection(Long topicId, String memberId) {
        var topic = topicService.requireById(topicId);
        var votesUrl = url("/api/v1/topics/" + topicId + "/votes");
        var options = List.of(
                new SelectionItem("Sim", votesUrl, voteBody(memberId, VoteChoice.YES)),
                new SelectionItem("Não", votesUrl, voteBody(memberId, VoteChoice.NO))
        );
        return Screen.selection("Votar: " + topic.getTitle(), options);
    }

    private Map<String, Object> voteBody(String memberId, VoteChoice choice) {
        return Map.of("memberId", memberId, "choice", choice.name());
    }

    private String url(String path) {
        return properties.baseUrl() + path;
    }
}