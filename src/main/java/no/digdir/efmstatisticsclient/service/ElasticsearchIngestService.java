package no.digdir.efmstatisticsclient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.efmstatisticsclient.domain.data.EsIndexDTO;
import no.digdir.efmstatisticsclient.domain.data.HitDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchIngestService {
    private final ElasticsearchClientImpl client;
    List<HitDTO> hits = new ArrayList<>();

    public void getLogsFromIndex(String index) {
        EsIndexDTO esIndexDTO = client.openScrollIndex(index);

        if (esIndexDTO.getScrollId() != null) {
            String scrollId = esIndexDTO.getScrollId();
            log.trace("ScrollId is: " + scrollId);

            hits = esIndexDTO.getHits().getHitDtoList();
            getNextScrollFromIndex(scrollId, esIndexDTO);
            log.info("Retrieved {} out of {} available log events", hits.size(), esIndexDTO.getHits().getTotal());

            if(client.clearScroll(scrollId).isSucceeded()) {
                log.trace("Successfully cleared scroll. Ready for another index");
            }
        }
    }

    private void getNextScrollFromIndex(String scrollId, EsIndexDTO dto) {
        while (dto.getHits().getTotal() > hits.size()) {
            if (scrollId.length() > 1) {
                EsIndexDTO nextScroll = client.getNextScroll(scrollId);
//TODO putt i liste med streams api?
                for (HitDTO h : nextScroll.getHits().getHitDtoList()) {
                    hits.add(h);
                    log.trace("adding to hits list: {}", h);
                }
            } else {
                break;
            }
            System.out.println("While ran a loop and hits has " + hits.size() + " elements.");
        }
    }
}
