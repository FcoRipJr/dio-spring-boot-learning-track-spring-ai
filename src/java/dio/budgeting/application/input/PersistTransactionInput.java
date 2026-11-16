package dio.budgeting.application.input;

import dio.budgeting.domain.Category;

import java.time.LocalDate;

import org.springframework.ai.tool.annotation.ToolParam;

public record PersistTransactionInput(@ToolParam(description = "Descrição do gasto") String description,
                                      @ToolParam(description = "Valor do gasto (em centavos)") long amount,
                                      @ToolParam(description = "Categoria de uma transação") Category category,
                                      @ToolParam(description = "Data de uma transação no formato YYYY-MM-DD. Informe apenas se o usário mencionar a data, senão omita.", required = false) LocalDate date) {
}
