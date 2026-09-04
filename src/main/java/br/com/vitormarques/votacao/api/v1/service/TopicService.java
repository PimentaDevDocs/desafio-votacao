package br.com.vitormarques.votacao.api.v1.service;

import br.com.vitormarques.votacao.api.v1.dto.CreateTopicRequest;
import br.com.vitormarques.votacao.api.v1.dto.TopicResponse;

import br.com.vitormarques.votacao.api.v1.entity.Topic;
import br.com.vitormarques.votacao.api.v1.exception.TopicNotFoundException;
import br.com.vitormarques.votacao.api.v1.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final TopicRepository repository;

    @Transactional
    public TopicResponse create(CreateTopicRequest request) {
        var topic = repository.save(new Topic(request.title(), request.description()));
        log.info("Topic created id={}", topic.getId());
        return TopicResponse.from(topic);
    }

    @Transactional(readOnly = true)
    public TopicResponse findById(Long id) {
        return TopicResponse.from(requireById(id));
    }

    @Transactional(readOnly = true)
    public Topic requireById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));
    }
}