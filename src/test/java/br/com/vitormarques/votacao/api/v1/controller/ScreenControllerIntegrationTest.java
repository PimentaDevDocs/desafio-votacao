package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.entity.Topic;
import br.com.vitormarques.votacao.api.v1.repository.TopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ScreenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void shouldBuildNewTopicForm() throws Exception {
        mockMvc.perform(get("/api/v1/screens/topics/new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
                .andExpect(jsonPath("$.itens[0].tipo").value("INPUT_TEXTO"))
                .andExpect(jsonPath("$.itens[0].id").value("title"))
                .andExpect(jsonPath("$.itens[1].id").value("description"))
                .andExpect(jsonPath("$.botaoOk.url").value("http://test.local/api/v1/topics"))
                .andExpect(jsonPath("$.botaoCancelar").doesNotExist());
    }

    @Test
    void shouldBuildOpenSessionForm() throws Exception {
        var topic = topicRepository.save(new Topic("Reforma do estatuto", null));

        mockMvc.perform(get("/api/v1/screens/topics/" + topic.getId() + "/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
                .andExpect(jsonPath("$.itens[0].tipo").value("TEXTO"))
                .andExpect(jsonPath("$.itens[0].texto").value("Reforma do estatuto"))
                .andExpect(jsonPath("$.itens[1].tipo").value("INPUT_NUMERICO"))
                .andExpect(jsonPath("$.itens[1].id").value("durationMinutes"))
                .andExpect(jsonPath("$.botaoOk.url")
                        .value("http://test.local/api/v1/topics/" + topic.getId() + "/session"));
    }

    @Test
    void shouldBuildVoteSelection() throws Exception {
        var topic = topicRepository.save(new Topic("Reforma do estatuto", null));
        var votesUrl = "http://test.local/api/v1/topics/" + topic.getId() + "/votes";

        mockMvc.perform(get("/api/v1/screens/topics/" + topic.getId() + "/vote")
                        .param("memberId", "52998224725"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("SELECAO"))
                .andExpect(jsonPath("$.titulo").value("Votar: Reforma do estatuto"))
                .andExpect(jsonPath("$.itens.length()").value(2))
                .andExpect(jsonPath("$.itens[0].texto").value("Sim"))
                .andExpect(jsonPath("$.itens[0].url").value(votesUrl))
                .andExpect(jsonPath("$.itens[0].body.memberId").value("52998224725"))
                .andExpect(jsonPath("$.itens[0].body.choice").value("YES"))
                .andExpect(jsonPath("$.itens[1].texto").value("Não"))
                .andExpect(jsonPath("$.itens[1].body.choice").value("NO"));
    }

    @Test
    void shouldRequireMemberIdForVoteSelection() throws Exception {
        var topic = topicRepository.save(new Topic("Qualquer", null));

        mockMvc.perform(get("/api/v1/screens/topics/" + topic.getId() + "/vote"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForUnknownTopic() throws Exception {
        mockMvc.perform(get("/api/v1/screens/topics/999999/vote").param("memberId", "1"))
                .andExpect(status().isNotFound());
    }
}