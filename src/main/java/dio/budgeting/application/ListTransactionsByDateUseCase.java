package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListTransactionsByDateUseCase {
    private final TransactionRepository transactionRepository;

    public ListTransactionsByDateUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "list-transactions-by-date", description = "Lista transações financeiras por data")
    public List<TransactionOutput> execute(@ToolParam(description = "Data de uma transação no formato YYYY-MM-DD") LocalDate date) {
        return transactionRepository.findAllByDate(date).stream().map(TransactionOutput::from).toList();
    }
}
