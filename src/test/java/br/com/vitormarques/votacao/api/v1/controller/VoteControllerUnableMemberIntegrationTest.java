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

@SpringBootTest(properties = "app.eligibility.able-rate=0")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VoteControllerUnableMemberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VotingSessionRepository sessionRepository;

    @Test
    void shouldReturn404WhenMemberIsUnableToVote() throws Exception {
        var topic = topicRepository.save(new Topic("Pauta", null));
        sessionRepository.save(new VotingSession(topic, LocalDateTime.now(), Duration.ofMinutes(1)));

        mockMvc.perform(post("/api/v1/topics/" + topic.getId() + "/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memberId\": \"52998224725\", \"choice\": \"YES\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Member unable to vote: 52998224725"));
    }
}