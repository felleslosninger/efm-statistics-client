package no.digdir.efmstatisticsclient.handler;

import lombok.RequiredArgsConstructor;
import no.digdir.efmstatisticsclient.domain.data.HitDTO;
import no.digdir.efmstatisticsclient.router.EsIndexRouter;
import no.digdir.efmstatisticsclient.service.ElasticsearchIngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EsIndexHandler {
    private final ElasticsearchIngestService service;

    public Mono<ServerResponse> getEsIndex(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getLogsFromIndex(request.pathVariable("id")), HitDTO.class);
    }

    public Mono<ServerResponse> getTest(ServerRequest serverRequest) {
        return ServerResponse.ok().body("Hi world", String.class);
    }
}
