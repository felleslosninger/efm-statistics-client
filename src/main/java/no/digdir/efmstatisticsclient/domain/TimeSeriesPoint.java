package no.digdir.efmstatisticsclient.domain;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class TimeSeriesPoint {
    private ZonedDateTime timestamp;
    private Map<String, Long> measurements = new HashMap<>();
    private Map<String, String> categories;
}
