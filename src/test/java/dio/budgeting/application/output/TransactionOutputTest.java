package dio.budgeting.application.output;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionOutputTest {
    @ParameterizedTest 
    @CsvSource({
        "8000, 80.00",
        "1, 0.01",
        "12345, 123.45",
        "0, 0.00",
    }) 
    void sould_convertCentsToReais_whe_outputUsCreated(long cents, String expected) {
        var transaction = new Transaction("Test", cents, Category.GROCERIES, LocalDate.of(2026,11,14));
        var output = TransactionOutput.from(transaction);
        assertThat(output.value()).isEqualTo(new BigDecimal(expected));
    }

}
