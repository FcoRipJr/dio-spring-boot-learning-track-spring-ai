package dio.budgeting.infrastructure.ai;

import java.time.LocalDate;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class CurrentDateTool {
    @Tool(name = "get-current-date", description = "Retorna a data atual no formato YYYY-MM-DD. Utilize para interpretar datas relativas como hoje, ontem, semana passada, mês passado etc...")
    public String getCurrentDate(){
        return LocalDate.now().toString();
    }
}
