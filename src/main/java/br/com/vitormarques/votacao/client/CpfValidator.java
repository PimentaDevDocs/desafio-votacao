package br.com.vitormarques.votacao.client;

public final class CpfValidator {

    private CpfValidator() {
    }

    public static boolean isValid(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || allSameDigit(cpf)) {
            return false;
        }
        return checkDigit(cpf, 9) == cpf.charAt(9) - '0'
                && checkDigit(cpf, 10) == cpf.charAt(10) - '0';
    }

    public static String mask(String cpf) {
        return "***" + cpf.substring(cpf.length() - 3);
    }

    private static boolean allSameDigit(String cpf) {
        return cpf.chars().distinct().count() == 1;
    }

    private static int checkDigit(String cpf, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (cpf.charAt(i) - '0') * (length + 1 - i);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}