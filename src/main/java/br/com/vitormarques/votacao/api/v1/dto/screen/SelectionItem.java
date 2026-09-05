package br.com.vitormarques.votacao.api.v1.dto.screen;

import java.util.Map;

public record SelectionItem(String texto, String url, Map<String, Object> body) implements ScreenItem {
}