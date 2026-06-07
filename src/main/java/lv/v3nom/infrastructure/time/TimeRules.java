package lv.v3nom.infrastructure.time;

public record TimeRules() {
    // Time in milliseconds
    public static final long M_ONE_MINUTE     = 60000L;
    public static final long M_FIVE_MINUTES   = 300000L;
    public static final long M_HALF_AN_HOUR   = 1800000L;
    public static final long M_ONE_HOUR       = 3600000L;
    public static final long M_TWELVE_HOURS   = 43200000L;

    // Time in seconds
    public static final long S_ONE_MINUTE     = 60L;
    public static final long S_FIVE_MINUTES   = 300L;
    public static final long S_HALF_AN_HOUR   = 1800L;
    public static final long S_ONE_HOUR       = 3600L;
    public static final long S_TWELVE_HOURS   = 43200L;
}
