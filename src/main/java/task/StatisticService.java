package task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true)
@Component
public class StatisticService {

    @Value("${spring.nginxUri}")
    private String nginxServer;

    public String getNginxStatus() {
        try {
            HttpResponse<String> stringResponse = HttpClient
                    .newBuilder()
                    .proxy(ProxySelector.getDefault())
                    .build()
                    .send(HttpRequest.newBuilder()
                            .uri(new URI(nginxServer))
                            .GET()
                            .build(), HttpResponse.BodyHandlers.ofString());
            return stringResponse.body();
        } catch (URISyntaxException | IOException | InterruptedException e){
            throw new IllegalStateException(e);
        }
    }
}
