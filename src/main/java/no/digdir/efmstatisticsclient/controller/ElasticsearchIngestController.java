package no.digdir.efmstatisticsclient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.efmstatisticsclient.service.ElasticsearchIngestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class ElasticsearchIngestController {
    private final ElasticsearchIngestService elasticsearchIngestService;

    @GetMapping("/")
    public String getESIndex(@RequestParam(name = "index") String index) {
        elasticsearchIngestService.getLogsFromIndex(index);
        return index;

    }
}
