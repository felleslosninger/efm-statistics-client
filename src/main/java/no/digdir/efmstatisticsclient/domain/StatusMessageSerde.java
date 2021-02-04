package no.digdir.efmstatisticsclient.domain;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@RequiredArgsConstructor
public class StatusMessageSerde implements Serde<StatusMessage> {
    final private Serializer<StatusMessage> serializer;
    final private Deserializer<StatusMessage> deserializer;
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }
    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }
    @Override
    public Serializer<StatusMessage> serializer() {
        return serializer;
    }
    @Override
    public Deserializer<StatusMessage> deserializer() {
        return deserializer;
    }

}
