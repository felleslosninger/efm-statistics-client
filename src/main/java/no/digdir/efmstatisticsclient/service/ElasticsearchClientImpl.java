package no.digdir.efmstatisticsclient.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.digdir.efmstatisticsclient.config.StatisticsClientProperties;
import no.digdir.efmstatisticsclient.domain.data.EsIndexDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ElasticsearchClientImpl implements ElasticsearchClient {
    private final StatisticsClientProperties properties;
    private final WebClient webClient;


    public ElasticsearchClientImpl(StatisticsClientProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .exchangeStrategies(getExchangeStrategies())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(TcpClient
                        .create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getElasticsearch().connectTimeoutInMs)
                        .doOnConnected(connection -> {
                            connection.addHandlerLast(new ReadTimeoutHandler(properties.getElasticsearch().readTimeoutInMs, TimeUnit.MILLISECONDS));
                            connection.addHandlerLast(new WriteTimeoutHandler(properties.getElasticsearch().writeTimeoutInMs, TimeUnit.MILLISECONDS));
                        }))))
                .build();
    }

    //TODO sjekk om scrollen er open ?
    //TODO lukk scrollen etter at ein er ferdig.
    public EsIndexDTO openScrollIndex(String index) {
        URI uri = getScrollDownloadURI(index);
        log.trace("Fetching event data from Elasticsearch on URL: {}", uri);
        String initiateScroll = "{\n" +
                "  \"size\": 1000,\n" +
                "  \"query\": {\n" +
                "      \"match\": {\n" +
                "          \"logger_name\": \"STATUS\"\n" +
                "      }\n" +
                "  }\n" +
                "}";

        return webClient.post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(initiateScroll)
                .retrieve()
                .bodyToMono(EsIndexDTO.class)
                .block(Duration.of(properties.getElasticsearch().readTimeoutInMs, ChronoUnit.MILLIS));

    }

    @SneakyThrows(URISyntaxException.class)
    public URI getScrollDownloadURI(String index) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(properties.getElasticsearch().getEndpointURL().toURI())
                .path(index + "/" + "_search")
                .queryParam("scroll", "1m")
                .queryParam("pretty");

        URI uri = builder.build().toUri();
        log.trace("Built Elasticsearch Scroll API URL: {}", uri);
        return uri;
    }

    private ExchangeStrategies getExchangeStrategies() {
        ObjectMapper objectMapper = getObjectMapper();
        return ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                    config.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
                }).build();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    @SneakyThrows(URISyntaxException.class)
    public URI getNextScrollURI(String scrollId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(properties.getElasticsearch().getEndpointURL().toURI())
                .path("_search/scroll")
                .queryParam("scroll", "1m")
                .queryParam("scroll_id", scrollId)
                .queryParam("pretty");

        URI uri = builder.build().toUri();
        log.trace("Built Elasticsearch next scroll URL: {}", uri);
        return uri;
    }

    public EsIndexDTO getNextScroll(String scrollId) {
        log.trace("Attempting to fetch next scroll with id: {}", scrollId);

        return webClient.get()
                .uri(getNextScrollURI(scrollId))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(EsIndexDTO.class)
                .block(Duration.of(properties.getElasticsearch().readTimeoutInMs, ChronoUnit.MILLIS));
    }
}

//TODO sjekk webclient templates.