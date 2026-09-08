package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.entity.Topic;
import br.com.vitormarques.votacao.entity.Vote;
import br.com.vitormarques.votacao.entity.VotingSession;
import br.com.vitormarques.votacao.enums.VoteChoice;
import br.com.vitormarques.votacao.repository.TopicRepository;
import br.com.vitormarques.votacao.repository.VoteRepository;
import br.com.vitormarques.votacao.repository.VotingSessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VotingResultControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VotingSessionRepository sessionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void shouldReturnPartialCountWhileSessionIsOpen() throws Exception {
        var session = sessionOpenedAt(LocalDateTime.now());
        vote(session, "1", VoteChoice.YES);
        vote(session, "2", VoteChoice.NO);

        mockMvc.perform(get(resultUrl(session)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionStatus").value("OPEN"))
                .andExpect(jsonPath("$.yes").value(1))
                .andExpect(jsonPath("$.no").value(1))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    void shouldApproveWhenSessionClosedWithMoreYes() throws Exception {
        var session = sessionOpenedAt(LocalDateTime.now().minusMinutes(10));
        vote(session, "1", VoteChoice.YES);
        vote(session, "2", VoteChoice.YES);
        vote(session, "3", VoteChoice.NO);

        mockMvc.perform(get(resultUrl(session)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionStatus").value("CLOSED"))
                .andExpect(jsonPath("$.yes").value(2))
                .andExpect(jsonPath("$.no").value(1))
                .andExpect(jsonPath("$.result").value("APPROVED"));
    }

    @Test
    void shouldReturnZeroCountsWhenNoVotes() throws Exception {
        var session = sessionOpenedAt(LocalDateTime.now().minusMinutes(10));

        mockMvc.perform(get(resultUrl(session)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yes").value(0))
                .andExpect(jsonPath("$.no").value(0))
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.result").value("TIED"));
    }

    @Test
    void shouldReturn404WhenSessionNotOpened() throws Exception {
        var topic = topicRepository.save(new Topic("Sem sessão", null));

        mockMvc.perform(get("/api/v1/topics/" + topic.getId() + "/result"))
                .andExpect(status().isNotFound());
    }

    private VotingSession sessionOpenedAt(LocalDateTime openedAt) {
        var topic = topicRepository.save(new Topic("Pauta", null));
        return sessionRepository.save(new VotingSession(topic, openedAt, Duration.ofMinutes(1)));
    }

    private void vote(VotingSession session, String memberId, VoteChoice choice) {
        voteRepository.save(new Vote(session, memberId, choice));
    }

    private String resultUrl(VotingSession session) {
        return "/api/v1/topics/" + session.getTopic().getId() + "/result";
    }
}