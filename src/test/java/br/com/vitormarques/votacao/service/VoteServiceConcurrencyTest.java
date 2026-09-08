package br.com.vitormarques.votacao.service;

import br.com.vitormarques.votacao.api.v1.dto.VoteRequest;
import br.com.vitormarques.votacao.entity.Topic;
import br.com.vitormarques.votacao.entity.VotingSession;
import br.com.vitormarques.votacao.enums.VoteChoice;
import br.com.vitormarques.votacao.exception.MemberAlreadyVotedException;
import br.com.vitormarques.votacao.repository.TopicRepository;
import br.com.vitormarques.votacao.repository.VoteRepository;
import br.com.vitormarques.votacao.repository.VotingSessionRepository;
import br.com.vitormarques.votacao.support.CpfGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VoteServiceConcurrencyTest {

    private static final int THREADS = 20;

    @Autowired
    private VoteService voteService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private VotingSessionRepository sessionRepository;

    @Autowired
    private VoteRepository voteRepository;

    private Long topicId;

    @BeforeEach
    void openSession() {
        var topic = topicRepository.save(new Topic("Concorrência", null));
        sessionRepository.save(new VotingSession(topic, LocalDateTime.now(), Duration.ofMinutes(5)));
        topicId = topic.getId();
    }

    @AfterEach
    void cleanUp() {
        voteRepository.deleteAll();
        sessionRepository.deleteAll();
        topicRepository.deleteAll();
    }

    @Test
    void shouldPersistOnlyOneVoteWhenSameMemberVotesConcurrently() throws InterruptedException {
        var sameCpf = CpfGenerator.fromBase(1);
        var registered = new AtomicInteger();
        var rejected = new AtomicInteger();

        voteConcurrently(i -> sameCpf, registered, rejected);

        assertThat(registered).hasValue(1);
        assertThat(rejected).hasValue(THREADS - 1);
        assertThat(voteRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldPersistAllVotesWhenDifferentMembersVoteConcurrently() throws InterruptedException {
        var registered = new AtomicInteger();
        var rejected = new AtomicInteger();

        voteConcurrently(i -> CpfGenerator.fromBase(i + 1), registered, rejected);
        assertThat(registered).hasValue(THREADS);
        assertThat(rejected).hasValue(0);
        assertThat(voteRepository.count()).isEqualTo(THREADS);
    }

    private void voteConcurrently(IntFunction<String> cpfForThread,
                                  AtomicInteger registered,
                                  AtomicInteger rejected) throws InterruptedException {
        var startSignal = new CountDownLatch(1);
        var done = new CountDownLatch(THREADS);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < THREADS; i++) {
                var request = new VoteRequest(cpfForThread.apply(i), VoteChoice.YES);
                executor.submit(() -> {
                    try {
                        startSignal.await();
                        voteService.register(topicId, request);
                        registered.incrementAndGet();
                    } catch (MemberAlreadyVotedException ex) {
                        rejected.incrementAndGet();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            startSignal.countDown();
            assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
        }
    }
}