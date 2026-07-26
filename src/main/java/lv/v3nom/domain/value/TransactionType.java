package lv.v3nom.domain.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class TransactionType {
    private final String transactionCode;
    private final String transactionName;
    private final boolean affectsBalance;
    private final boolean requiresRecipient;

    private static final Map<String, TransactionType> CACHE = new HashMap<>();

    private TransactionType(
            String transactionCode,
            String transactionName,
            boolean affectsBalance,
            boolean requiresRecipient) {

        if (transactionCode == null) {
            throw new IllegalArgumentException("Transaction Type Code cannot be null");
        }
        if (transactionCode.isBlank()) {
            throw new IllegalArgumentException("Transaction Type Code cannot be blank");
        }
        if (transactionName == null) {
            throw new IllegalArgumentException("Transaction Type Name cannot be null");
        }
        if (transactionName.isBlank()) {
            throw new IllegalArgumentException("Transaction Type Name cannot be blank");
        }

        this.transactionCode = transactionCode;
        this.transactionName = transactionName;
        this.affectsBalance = affectsBalance;
        this.requiresRecipient = requiresRecipient;
    }

    public static final TransactionType DEPOSIT =
            new TransactionType("DEP", "DEPOSIT", true, false);
    public static final TransactionType WITHDRAW =
            new TransactionType("WDR", "WITHDRAW", true, false);
    public static final TransactionType TRANSFER =
            new TransactionType("TFR", "TRANSFER", true, true);
    public static final TransactionType FEE =
            new TransactionType("FEE", "FEE", true, false);
    public static final TransactionType INTEREST =
            new TransactionType("INT", "INTEREST", true, false);

    static {
        CACHE.put("DEPOSIT", DEPOSIT);
        CACHE.put("WITHDRAW", WITHDRAW);
        CACHE.put("TRANSFER", TRANSFER);
        CACHE.put("FEE", FEE);
        CACHE.put("INTEREST", INTEREST);
    }
    public static TransactionType of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("of() -> TransactionType cannot be null");
        }

        TransactionType type = CACHE.get(value);
        if (type == null) {
            throw new IllegalArgumentException("of() -> Wrong TransactionType format: " + value);
        }
        return type;
    }
    public boolean affectsBalance() {
        return affectsBalance;
    }
    public boolean requiresRecipient() {
        return requiresRecipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionType that = (TransactionType) o;
        return transactionCode.equals(that.transactionCode);
    }
    @Override
    public int hashCode() { return Objects.hash(transactionCode); }
    @Override
    public String toString() { return transactionName; }

    public String getTransactionCode() { return transactionCode; }
    public String getTransactionName() { return transactionName; }
}
