package br.com.vitormarques.votacao.api.v1.dto;

import br.com.vitormarques.votacao.enums.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VoteRequest(
        @Schema(description = "Member's CPF, digits only", example = "")
        @NotBlank @Size(max = 20) String memberId,
        @NotNull VoteChoice choice
) {
}