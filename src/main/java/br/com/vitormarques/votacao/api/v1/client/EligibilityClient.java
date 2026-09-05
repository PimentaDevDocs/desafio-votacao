package br.com.vitormarques.votacao.api.v1.client;

import br.com.vitormarques.votacao.api.v1.enums.EligibilityStatus;

/**
 * Consulta se um associado identificado pelo CPF, pode votar.
 * Lança {@link InvalidCpfException} quando o CPF não existe.
 */
public interface EligibilityClient {

    EligibilityStatus check(String cpf);
}