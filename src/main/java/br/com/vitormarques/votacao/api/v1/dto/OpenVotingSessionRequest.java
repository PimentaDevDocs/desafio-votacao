package br.com.vitormarques.votacao.api.v1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.Duration;

public record OpenVotingSessionRequest(
        @Min(1) @Max(1440) Integer durationMinutes
) {

    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(1);

    public Duration duration() {
        return durationMinutes == null ? DEFAULT_DURATION : Duration.ofMinutes(durationMinutes);
    }
}