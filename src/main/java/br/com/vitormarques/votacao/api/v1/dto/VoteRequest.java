package br.com.vitormarques.votacao.api.v1.dto;

import br.com.vitormarques.votacao.api.v1.enums.VoteChoice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VoteRequest(
        @NotBlank @Size(max = 20) String memberId,
        @NotNull VoteChoice choice
) {
}