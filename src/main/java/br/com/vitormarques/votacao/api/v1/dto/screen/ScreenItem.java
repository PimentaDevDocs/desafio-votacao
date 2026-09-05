package br.com.vitormarques.votacao.api.v1.dto.screen;

/**
 * Item de uma tela do cliente mobile (Anexo 1).
 * FORMULARIO usa FormField; SELECAO usa SelectionItem.
 */
public sealed interface ScreenItem permits FormField, SelectionItem {
}