package br.com.vitormarques.votacao.api.v1.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VotingSessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String sessionUrl;

    @BeforeEach
    void createTopic() throws Exception {
        var topicLocation = mockMvc.perform(post("/api/v1/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Pauta com sessão\"}"))
                .andReturn().getResponse().getHeader("Location");
        sessionUrl = topicLocation + "/session";
    }

    @Test
    void shouldOpenSessionWithDefaultDuration() throws Exception {
        var body = mockMvc.perform(post(sessionUrl))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();

        var openedAt = LocalDateTime.parse(JsonPath.read(body, "$.openedAt"));
        var closesAt = LocalDateTime.parse(JsonPath.read(body, "$.closesAt"));
        assertThat(Duration.between(openedAt, closesAt)).isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    void shouldOpenSessionWithCustomDuration() throws Exception {
        var body = mockMvc.perform(post(sessionUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationMinutes\": 5}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var openedAt = LocalDateTime.parse(JsonPath.read(body, "$.openedAt"));
        var closesAt = LocalDateTime.parse(JsonPath.read(body, "$.closesAt"));
        assertThat(Duration.between(openedAt, closesAt)).isEqualTo(Duration.ofMinutes(5));
    }

    @Test
    void shouldRejectInvalidDuration() throws Exception {
        mockMvc.perform(post(sessionUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationMinutes\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("durationMinutes"));
    }

    @Test
    void shouldNotOpenSessionTwice() throws Exception {
        mockMvc.perform(post(sessionUrl)).andExpect(status().isCreated());

        mockMvc.perform(post(sessionUrl))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturn404WhenTopicDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/v1/topics/999999/session"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindOpenedSession() throws Exception {
        mockMvc.perform(post(sessionUrl));

        mockMvc.perform(get(sessionUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.closesAt").exists());
    }

    @Test
    void shouldReturn404WhenSessionNotOpened() throws Exception {
        mockMvc.perform(get(sessionUrl))
                .andExpect(status().isNotFound());
    }
}