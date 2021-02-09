package no.digdir.efmstatisticsclient.config;

import no.digdir.efmstatisticsclient.domain.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

@Configuration
public class StatisticsClientBeans {

    @Bean
    public Properties consumerProperties(StatisticsClientProperties statisticsClientProperties) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, statisticsClientProperties.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, statisticsClientProperties.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, statisticsClientProperties.getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, statisticsClientProperties.getAutoOffsetReset());
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, statisticsClientProperties.getCacheMaxBytesBuffer());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, KeySerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass());

        return props;
    }

    @Bean
    public Serde<StatusMessage> statusMessageSerde() {
        Map<String, Object> properties = Map.of("json.value.type", StatusMessage.class);
        EfmKafkaJsonSerializer<StatusMessage> serializer = new EfmKafkaJsonSerializer<>();
        serializer.configure(properties, false);
        EfmKafkaJsonDeserializer<StatusMessage> deserializer = new EfmKafkaJsonDeserializer<>();
        deserializer.configure(properties, false);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    @Bean
    public Serde<StatusKey> statusKeySerde() {
        Map<String, Object> keyProps = Map.of("json.value.type", StatusKey.class);
        EfmKafkaJsonSerializer<StatusKey> keySerializer = new EfmKafkaJsonSerializer<>();
        keySerializer.configure(keyProps, false);
        EfmKafkaJsonDeserializer<StatusKey> keyDeserializer = new EfmKafkaJsonDeserializer<>();
        keyDeserializer.configure(keyProps, false);
        return Serdes.serdeFrom(keySerializer, keyDeserializer);
    }

}
