package br.com.vitormarques.votacao.client;

import br.com.vitormarques.votacao.config.AppProperties;
import br.com.vitormarques.votacao.enums.EligibilityStatus;
import br.com.vitormarques.votacao.exception.InvalidCpfException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FakeEligibilityClientTest {

    private static final String VALID_CPF = "52998224725";

    @Test
    void shouldReturnAbleWhenRateIsOne() {
        var client = clientWithRate(1.0);

        assertThat(client.check(VALID_CPF)).isEqualTo(EligibilityStatus.ABLE_TO_VOTE);
    }

    @Test
    void shouldReturnUnableWhenRateIsZero() {
        var client = clientWithRate(0.0);

        assertThat(client.check(VALID_CPF)).isEqualTo(EligibilityStatus.UNABLE_TO_VOTE);
    }

    @Test
    void shouldRejectInvalidCpf() {
        assertThatThrownBy(() -> clientWithRate(1.0).check("11111111111"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldMaskAllButLastThreeDigits() {
        assertThat(FakeEligibilityClient.mask(VALID_CPF)).isEqualTo("***725");
    }

    private FakeEligibilityClient clientWithRate(double rate) {
        return new FakeEligibilityClient(new AppProperties("http://x", new AppProperties.Eligibility(rate)));
    }
}