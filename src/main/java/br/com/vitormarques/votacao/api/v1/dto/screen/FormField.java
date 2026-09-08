package br.com.vitormarques.votacao.api.v1.dto.screen;

import br.com.vitormarques.votacao.enums.FieldType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FormField(FieldType tipo, String id, String titulo, String texto) implements ScreenItem {

    public static FormField text(String id, String titulo) {
        return new FormField(FieldType.INPUT_TEXTO, id, titulo, null);
    }

    public static FormField number(String id, String titulo) {
        return new FormField(FieldType.INPUT_NUMERICO, id, titulo, null);
    }

    public static FormField label(String texto) {
        return new FormField(FieldType.TEXTO, null, null, texto);
    }
}