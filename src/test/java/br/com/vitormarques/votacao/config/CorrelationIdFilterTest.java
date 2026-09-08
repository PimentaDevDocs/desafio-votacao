package br.com.vitormarques.votacao.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorrelationIdFilterTest {

    private static final String UUID_PATTERN =
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldEchoCorrelationIdFromRequest() throws Exception {
        mockMvc.perform(get("/api/v1/topics/1").header(CorrelationIdFilter.HEADER, "abc-123"))
                .andExpect(header().string(CorrelationIdFilter.HEADER, "abc-123"));
    }

    @Test
    void shouldGenerateCorrelationIdWhenAbsent() throws Exception {
        mockMvc.perform(get("/api/v1/topics/1"))
                .andExpect(header().string(CorrelationIdFilter.HEADER, matchesPattern(UUID_PATTERN)));
    }
}