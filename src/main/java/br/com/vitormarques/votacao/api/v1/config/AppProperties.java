package br.com.vitormarques.votacao.api.v1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String baseUrl, Eligibility eligibility) {

    public record Eligibility(double ableRate) {
    }
}