package br.com.vitormarques.votacao.api.v1.support;

public final class CpfGenerator {

    private CpfGenerator() {
    }

    /**
     * Gera um CPF válido.
     */
    public static String fromBase(int base) {
        var digits = String.format("%09d", base);
        var first = checkDigit(digits);
        var second = checkDigit(digits + first);
        return digits + first + second;
    }

    private static int checkDigit(String digits) {
        int weight = digits.length() + 1;
        int sum = 0;
        for (char c : digits.toCharArray()) {
            sum += (c - '0') * weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}