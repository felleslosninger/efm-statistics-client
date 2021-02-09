package no.digdir.efmstatisticsclient.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusKey {
    String orgnr;
    Status status;
    String service_identifier;
    Long window_start;
    Long window_end;
}
