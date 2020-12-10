package task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class StatisticController {

    private StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping(value = "/nginx/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> get(){
        String nsginxStatus = statisticService.getNginxStatus();
        String[] stringLines = nsginxStatus.split("\\n");
        if(!(stringLines[0].startsWith("Active") || stringLines[0].startsWith("active"))) {
            throw new IllegalStateException("bad nginx status format");
        }
        TreeMap<String, Object> result = new TreeMap<>();
        result.put("activeConnections", Integer.parseInt(stringLines[0].split(" ")[2]));
        String[] requests = stringLines[2].split(" ");
        result.put("requests",
                new TreeMap<>(Map.of("accepts", Integer.parseInt(requests[1]),
                        "handled", Integer.parseInt(requests[2]),
                        "requests", Integer.parseInt(requests[3]))));
        return result;
    }
}
