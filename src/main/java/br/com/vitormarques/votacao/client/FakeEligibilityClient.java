package br.com.vitormarques.votacao.client;

import br.com.vitormarques.votacao.config.AppProperties;
import br.com.vitormarques.votacao.enums.EligibilityStatus;
import br.com.vitormarques.votacao.exception.InvalidCpfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.random.RandomGenerator;

/**
 * Client fake do bônus 1. Valida os dígitos e o ABLE/UNABLE é sorteado com o app.eligibility.able-rate
 * A integração real utilizando a interface EligibilityClient substitui essa classe sem alterar o VoteService
 */
@Component
@Slf4j
public class FakeEligibilityClient implements EligibilityClient {

    private final double ableRate;
    private final RandomGenerator random = RandomGenerator.getDefault();

    public FakeEligibilityClient(AppProperties properties) {
        this.ableRate = properties.eligibility().ableRate();
    }

    @Override
    public EligibilityStatus check(String cpf) {
        if (!CpfValidator.isValid(cpf)) {
            throw new InvalidCpfException(cpf);
        }
        var status = random.nextDouble() < ableRate
                ? EligibilityStatus.ABLE_TO_VOTE
                : EligibilityStatus.UNABLE_TO_VOTE;
        log.debug("Eligibility checked cpf={} status={}", CpfValidator.mask(cpf), status);
        return status;
    }
}