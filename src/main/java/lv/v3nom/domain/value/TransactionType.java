package lv.v3nom.domain.value;

import java.util.Objects;

public class TransactionType {
    private final String transactionCode;
    private final String transactionName;
    private final boolean affectsBalance;
    private final boolean requiresRecipient;

    private TransactionType(
            String transactionCode,
            String transactionName,
            boolean affectsBalance,
            boolean requiresRecipient
    ) {
        if (transactionCode == null || transactionCode.isBlank()) {
            throw new IllegalArgumentException("Transaction type cannot be null or blank");
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

    public String getTransactionCode() {
        return transactionCode;
    }
    public String getTransactionName() {
        return transactionName;
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
}
