package lv.v3nom.common.rules;

import java.util.regex.Pattern;

public final class IdGenerationRules {
    public static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "0123456789";
    public static final String DIGITS =
            "0123456789";
    public static final String SPECIAL =
            "!@#$&*";
    public static final Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
    );

    private IdGenerationRules() {}
}
