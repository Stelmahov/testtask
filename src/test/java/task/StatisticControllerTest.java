package task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StatisticController.class)
public class StatisticControllerTest {

    @Autowired
    protected MockMvc mvc;

    @MockBean
    private StatisticService statisticService;

    String nginxStatus = "Active connections: 2\n" +
            "server accepts handled requests\n" +
            " 841845 841845 1631067\n" +
            "Reading: 0 Writing: 1 Waiting: 1";

    @Test
    public void testGetStatus() throws Exception {
        Mockito.when(statisticService.getNginxStatus()).thenReturn(nginxStatus);

        final String actual = mvc.perform(get("/api/stat")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println(actual);
        Assertions.assertEquals(actual, "{\"activeConnections\":2," +
                "\"requests\":{" +
                "\"accepts\":841845," +
                "\"handled\":841845," +
                "\"requests\":1631067" +
                "}}");
    }

    @Test
    public void testContentType() throws Exception {
        Mockito.when(statisticService.getNginxStatus()).thenReturn(nginxStatus);

        final String actual = mvc.perform(get("/api/stat")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentType();

        Assertions.assertEquals(actual, "application/json");
    }

    @Test
    public void testInvalidStatusFormat() {
        Mockito.when(statisticService.getNginxStatus()).thenReturn(" , ,");

        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class,
                () -> new StatisticController(statisticService).get());
        Assertions.assertEquals("bad nginx status format", thrown.getMessage());
    }
}
