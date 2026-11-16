package dio.budgeting.infrastructure.http;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest 
@Transactional 
public class TransactionControllerDateTest {

    private static final String TEST_DATE = "2000-01-15";

    @Autowired 
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach 
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test 
    void should_keepInformedDate_when_transactionIsCreatedWithDate() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"description": "pharmacy test", "category": "PHARMA", "amount": 4000, "date": "%s"}
                            """.formatted(TEST_DATE)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(TEST_DATE));
    }

    @Test
    void shoul_useCurrentDate_when_transactionIsCreatedWithoutDate() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"description": "groceries test", "category": "GROCERIES", "amount": 8000}
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()));
    }

    @Test
    void should_listCreatedTransaction_when_searchingByItsDate() throws Exception {
        var body = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"description": "pharmacy test", "category": "PHARMA", "amount": 4000, "date": "%s"}
                            """.formatted(TEST_DATE)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(body, "$.id");

        mockMvc.perform(get("/transactions").param("date",TEST_DATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(id)))
                .andExpect(jsonPath("$[*].date", everyItem(is(TEST_DATE))));
    }
}
