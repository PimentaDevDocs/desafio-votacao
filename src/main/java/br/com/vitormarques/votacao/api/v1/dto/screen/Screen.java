package br.com.vitormarques.votacao.api.v1.dto.screen;

import br.com.vitormarques.votacao.api.v1.enums.ScreenType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Tela do cliente mobile. Nomes dos campos seguem o contrato do Anexo 1.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Screen(
        ScreenType tipo,
        String titulo,
        List<? extends ScreenItem> itens,
        Button botaoOk,
        Button botaoCancelar
) {

    public static Screen form(String titulo, List<FormField> itens, Button botaoOk) {
        return new Screen(ScreenType.FORMULARIO, titulo, itens, botaoOk, null);
    }

    public static Screen selection(String titulo, List<SelectionItem> itens) {
        return new Screen(ScreenType.SELECAO, titulo, itens, null, null);
    }
}