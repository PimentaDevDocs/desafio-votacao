package br.com.vitormarques.votacao.api.v1.dto;


import br.com.vitormarques.votacao.api.v1.entity.Topic;

import java.time.LocalDateTime;

public record TopicResponse(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt
) {

    public static TopicResponse from(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getCreatedAt()
        );
    }
}