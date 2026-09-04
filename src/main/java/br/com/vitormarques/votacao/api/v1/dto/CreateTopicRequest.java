package br.com.vitormarques.votacao.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTopicRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description
) {
}