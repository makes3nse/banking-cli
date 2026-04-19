package lv.v3nom.common.util;

import lv.v3nom.common.rules.IdGenerationRules;

import java.util.Random;

public class IdGenerator {
    private static final Random random = new Random();
    private static final Integer length = 15;

    public static String generateDigitId() {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    public static String  generateAsciiId() {
        StringBuilder id = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(IdGenerationRules.CHARACTERS.length());
            id.append(IdGenerationRules.CHARACTERS.charAt(index));
        }
        return id.toString();
    }
}
