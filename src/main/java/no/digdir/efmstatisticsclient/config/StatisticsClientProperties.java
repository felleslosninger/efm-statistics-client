package no.digdir.efmstatisticsclient.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URL;

@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "digdir.statisticsclient")
@Data
public class StatisticsClientProperties {

    @Valid
    private ElasticsearchProperties elasticsearch;

    @NotNull
    private String applicationId;
    @NotNull
    private String bootstrapServer;
    @NotNull
    private String groupId;
    @NotNull
    private String autoOffsetReset;
    @NotNull
    private Integer cacheMaxBytesBuffer;
    @NotNull
    private Long windowSizeSeconds;
    @NotNull
    private String statusTopic;
    @NotNull
    private String countStore;
    @NotNull
    private String countTopic;
    @NotNull
    private Long retentionPeriod;
    @NotNull
    private Long suppressionPeriod;
    
    @NotNull
    private String ingestHost;
    @NotNull
    private String ingestSeriesName;
    @NotNull
    private String ingestUsername;
    @NotNull
    private String ingestPassword;

    //Elasticsearch ingest
    @Data
    public static class ElasticsearchProperties {
        @NotNull
        private URL endpointURL;
        @NotNull
        public long readTimeoutInMs;
        @NotNull
        public Integer connectTimeoutInMs;
        @NotNull
        public long writeTimeoutInMs;

    }
}



