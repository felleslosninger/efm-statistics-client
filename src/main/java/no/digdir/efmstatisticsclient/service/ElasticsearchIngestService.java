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
    private String scrollId = "";

    public void openIndex(String index) {
        EsIndexDTO esIndexDTO = client.openScrollIndex(index);

        if (esIndexDTO.getScrollId() != null) {
            scrollId = esIndexDTO.getScrollId();
            log.trace("ScrollId is: " + scrollId);
        }
        hits = esIndexDTO.getHits().getHitDtoList();

        System.out.println("Scroll is created and Hits contains: ");
        for (HitDTO h : hits) {
            System.out.println(h + "\n");
        }

        getNextScrollFromIndex(scrollId, esIndexDTO);
        System.out.println("testing hits size when its ready to use: " + hits.size());
        //TODO clear scroll cleanup client.clearScroll(scrollId);
    }

    public void getNextScrollFromIndex(String scrollId, EsIndexDTO dto) {
        while (dto.getHits().getTotal() > hits.size()) {
            if (scrollId.length() > 1) {
                EsIndexDTO nextScroll = client.getNextScroll(scrollId);
                //nextScroll.getHits().getHitDtoList().stream().map(h -> hits.add(h));
                for (HitDTO h : nextScroll.getHits().getHitDtoList()) {
                    hits.add(h);
                    log.trace("adding to hits list: {}", h);
                }
            } else {
                break;
            }
            System.out.println("While ended and hits has " + hits.size() + " elements.");
        }
    }
}
