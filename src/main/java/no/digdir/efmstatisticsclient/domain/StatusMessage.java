package no.digdir.efmstatisticsclient.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StatusMessage {
    Status status;
    String conversation_id;
    String message_id;
    String orgnr;
    String process_identifier;
    String document_identifier;
    String sender;
    String receiver;
    String receiver_org_number;
    String sender_org_number;
    String service_identifier;
    String logger_name;
    String loglevel;
    LocalDateTime timestamp = LocalDateTime.now();
}
