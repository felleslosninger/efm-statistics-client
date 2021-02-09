package no.digdir.efmstatisticsclient.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.confluent.kafka.serializers.KafkaJsonDecoderConfig;
import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import lombok.Data;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

@Data
public class EfmKafkaJsonDeserializer<T> implements Deserializer<T> {
    ObjectMapper objectMapper = new ObjectMapper();
    Class<T> type;

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        this.configure(new KafkaJsonDeserializerConfig(props), isKey);
    }

    private void configure(KafkaJsonDecoderConfig config, Class<T> typez) {
        type = typez;
        objectMapper.registerModule(new JavaTimeModule());
        Boolean failUnknownProperties = config.getBoolean("json.fail.unknown.properties");
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failUnknownProperties);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public void configure(KafkaJsonDeserializerConfig config, boolean isKey) {
        if(isKey) {
            configure(config, (Class<T>) config.getClass("json.key.type"));
        } else {
            configure(config, (Class<T>) config.getClass("json.value.type"));
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if(data != null && data.length > 0) {
            try {
                return objectMapper.readValue(data, type);
            } catch (Exception e) {
                throw new SerializationException(e);
            }
        } else {
            return null;
        }
    }

 /*   @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return null;
    }*/

    @Override
    public void close() {

    }
}
