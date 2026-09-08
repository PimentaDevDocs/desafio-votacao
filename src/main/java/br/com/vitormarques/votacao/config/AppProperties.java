package br.com.vitormarques.votacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String baseUrl, Eligibility eligibility) {

    public record Eligibility(double ableRate) {
    }
}