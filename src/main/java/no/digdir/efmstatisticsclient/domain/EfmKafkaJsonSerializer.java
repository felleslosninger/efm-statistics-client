package no.digdir.efmstatisticsclient.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.confluent.kafka.serializers.KafkaJsonSerializerConfig;
import lombok.Data;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Data
public class EfmKafkaJsonSerializer<T> implements Serializer<T> {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> config, boolean isKey) {
        configure(new KafkaJsonSerializerConfig(config));
    }

    public void configure(KafkaJsonSerializerConfig config) {
        Boolean prettyPrint = config.getBoolean("json.indent.output");
        objectMapper.registerModules(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        } else {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Error serializing JSON message", e);
            }
        }
    }

    @Override
    public void close() {

    }
}
