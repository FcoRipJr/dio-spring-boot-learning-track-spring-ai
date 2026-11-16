package dio.budgeting.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Transaction {
    private TransactionId id;
    private String description;
    private long amount;
    private Category category;
    private LocalDate date;

    public Transaction(String description, long amount, Category category, LocalDate date) {
        this.id = new TransactionId();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = (date != null) ? date : LocalDate.now();
    }
}
