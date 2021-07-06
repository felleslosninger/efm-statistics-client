package no.digdir.efmstatisticsclient.router;

import no.digdir.efmstatisticsclient.handler.EsIndexHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Objects;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class EsIndexRouter {

    /**
     * The router configuration for EsIndexHandler
     */
    @Bean
    public RouterFunction<ServerResponse> esRoute(EsIndexHandler esIndexHandler) {
        return RouterFunctions
                .route(POST("/esindex")
                                .and(queryParam("index", Objects::nonNull))
                        ,esIndexHandler::getEsIndex)
                .andRoute(GET("esindex"), esIndexHandler::getTest);
    }


    /*

.and(accept(MediaType.APPLICATION_JSON)

        @Bean
    public RouterFunction<ServerResponse> esRoute(EsIndexHandler esIndexHandler) {
        return RouterFunctions.route()
                .path("/esindex", builder -> builder
                    .POST("",accept(MediaType.APPLICATION_JSON).and(queryParam("index", Objects::nonNull)), esIndexHandler::getEsIndex)).build();
    }
     */

}

