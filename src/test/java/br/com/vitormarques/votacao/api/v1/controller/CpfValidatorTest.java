package br.com.vitormarques.votacao.api.v1.controller;

import br.com.vitormarques.votacao.api.v1.client.CpfValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CpfValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "11144477735", "12345678909"})
    void shouldAcceptValidCpf(String cpf) {
        assertThat(CpfValidator.isValid(cpf)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224726", "11111111111", "1234567890", "529.982.247-25", "abcdefghijk"})
    void shouldRejectInvalidCpf(String cpf) {
        assertThat(CpfValidator.isValid(cpf)).isFalse();
    }

    @Test
    void shouldRejectNull() {
        assertThat(CpfValidator.isValid(null)).isFalse();
    }
}