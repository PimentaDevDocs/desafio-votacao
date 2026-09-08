package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.entity.Topic;
import br.com.vitormarques.votacao.entity.VotingSession;
import br.com.vitormarques.votacao.repository.TopicRepository;
import br.com.vitormarques.votacao.repository.VotingSessionRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VotingSessionRepository sessionRepository;

    @Test
    void shouldRegisterVote() throws Exception {
        var topic = topicWithSession(Duration.ofMinutes(1));

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": \"52998224725\", \"choice\": \"YES\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.topicId").value(topic.getId()))
                .andExpect(jsonPath("$.memberId").value("52998224725"))
                .andExpect(jsonPath("$.choice").value("YES"));
    }

    @Test
    void shouldRejectVoteWhenSessionIsClosed() throws Exception {
        var topic = topicWithClosedSession();

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": \"52998224725\", \"choice\": \"NO\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldReturn404WhenSessionNotOpened() throws Exception {
        var topic = topicRepository.save(new Topic("Sem sessão", null));

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": \"52998224725\", \"choice\": \"YES\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidChoice() throws Exception {
        var topic = topicWithSession(Duration.ofMinutes(1));

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": \"52998224725\", \"choice\": \"MAYBE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectVoteWithoutMember() throws Exception {
        var topic = topicWithSession(Duration.ofMinutes(1));

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"choice\": \"YES\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("memberId"));
    }

    private Topic topicWithSession(Duration duration) {
        var topic = topicRepository.save(new Topic("Pauta aberta", null));
        sessionRepository.save(new VotingSession(topic, LocalDateTime.now(), duration));
        return topic;
    }

    private Topic topicWithClosedSession() {
        var topic = topicRepository.save(new Topic("Pauta encerrada", null));
        var openedAt = LocalDateTime.now().minusMinutes(10);
        sessionRepository.save(new VotingSession(topic, openedAt, Duration.ofMinutes(1)));
        return topic;
    }

    private String votesUrl(Topic topic) {
        return "/api/v1/topics/" + topic.getId() + "/votes";
    }

    @Test
    void shouldRejectDuplicateVote() throws Exception {
        var topic = topicWithSession(Duration.ofMinutes(1));
        var body = "{\"memberId\": \"52998224725\", \"choice\": \"YES\"}";

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": \"52998224725\", \"choice\": \"NO\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Member 52998224725 already voted on topic: " + topic.getId()));
    }

    @Test
    void shouldReturn404ForInvalidCpf() throws Exception {
        var topic = topicWithSession(Duration.ofMinutes(1));

        mockMvc.perform(post(votesUrl(topic))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": \"11111111111\", \"choice\": \"YES\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("CPF not found: 11111111111"));
    }
}