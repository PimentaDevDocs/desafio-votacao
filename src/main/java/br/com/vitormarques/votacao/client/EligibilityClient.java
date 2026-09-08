package br.com.vitormarques.votacao.client;

import br.com.vitormarques.votacao.enums.EligibilityStatus;

/**
 * Consulta se um associado identificado pelo CPF, pode votar.
 * Lança {@link InvalidCpfException} quando o CPF não existe.
 */
public interface EligibilityClient {

    EligibilityStatus check(String cpf);
}