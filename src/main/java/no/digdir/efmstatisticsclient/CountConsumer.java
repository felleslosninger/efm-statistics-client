package no.digdir.efmstatisticsclient;

import lombok.RequiredArgsConstructor;
import no.digdir.efmstatisticsclient.domain.StatusKey;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class CountConsumer {
    private final Properties consumerProperties;
    private final Serde<StatusKey> statusKeySerde;

//    @PostConstruct
    public void statusCount() {

        StreamsBuilder builder = new StreamsBuilder();
        KStream<StatusKey, Long> countStream = builder.stream("status-count", Consumed.with(statusKeySerde, Serdes.Long()));
        countStream.foreach((k,v) -> {
            // TODO - process stream, push update to tidsserie
            System.out.println("Update - key="+k+", value="+v);
        });

        KafkaStreams streams = new KafkaStreams(builder.build(), consumerProperties);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
