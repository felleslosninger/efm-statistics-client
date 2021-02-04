package no.digdir.efmstatisticsclient.domain;

import org.apache.kafka.common.serialization.Serdes;

public class KeySerde extends Serdes.WrapperSerde<StatusKey> {
    public KeySerde() {
        super(new EfmKafkaJsonSerializer<>(), new EfmKafkaJsonDeserializer<>());
    }

}
