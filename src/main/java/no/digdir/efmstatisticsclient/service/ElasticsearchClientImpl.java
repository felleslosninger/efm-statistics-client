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
import no.digdir.efmstatisticsclient.domain.data.ClearScrollDTO;
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
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

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

    private ExchangeStrategies getExchangeStrategies() {
        ObjectMapper objectMapper = getObjectMapper();
        return ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
                    config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
                    config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                }).build();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    public EsIndexDTO openScrollIndex(String index) {
        URI uri = getScrollDownloadURI(index);
        log.trace("Fetching event data from Elasticsearch on URL: {}", uri);
        String initiateScroll = "{\n" +
                "  \"size\": 10000,\n" +
                "  \"query\": {\n" +
                "      \"match\": {\n" +
                "          \"logger_name\": \"STATUS\"\n" +
                "      }\n" +
                "  }\n" +
                "}";

        Mono<EsIndexDTO> esIndexDTOFlux = webClient.post()
                .uri(uri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(initiateScroll)
                .retrieve()
                .bodyToMono(EsIndexDTO.class);

        //esIndexDTOFlux.subscribe(System.out::println);
        esIndexDTOFlux.flatMap();
        return ;
        //TODO returnere objektet i monoen som er ein EsIndexDTO

        //.block(Duration.of(properties.getElasticsearch().readTimeoutInMs, ChronoUnit.MILLIS));

    }

    public EsIndexDTO getNextScroll(String scrollId) {
        log.trace("Attempting to fetch next scroll with id: {}", scrollId);

        return webClient.get()
                .uri(getNextScrollURI(scrollId))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(EsIndexDTO.class)
                .block();
                //.block(Duration.of(properties.getElasticsearch().readTimeoutInMs, ChronoUnit.MILLIS));
    }

    public ClearScrollDTO clearScroll(String scrollId) {
        log.debug("Clearing scroll with id: {}", scrollId);
        return webClient.delete()
                .uri(getDeleteScrollURI(scrollId))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(ClearScrollDTO.class)
                .block();
                //.block(Duration.of(properties.getElasticsearch().readTimeoutInMs, ChronoUnit.MILLIS));
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

    @SneakyThrows(URISyntaxException.class)
    private URI getDeleteScrollURI(String scrollId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(properties.getElasticsearch().getEndpointURL().toURI())
                .path("_search/scroll")
                .queryParam("scroll_id", scrollId)
                .queryParam("pretty");

        URI uri = builder.build().toUri();
        log.trace("Built Elasticsearch clear scroll URL: {}", uri);
        return uri;
    }
}

//TODO sjekk webclient templates.