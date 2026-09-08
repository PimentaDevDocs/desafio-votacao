package br.com.vitormarques.votacao.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI votingApi() {
        return new OpenAPI().info(new Info()
                .title("Voting API")
                .version("v1")
                .description("Topics and voting sessions management for cooperative assemblies."));
    }
}