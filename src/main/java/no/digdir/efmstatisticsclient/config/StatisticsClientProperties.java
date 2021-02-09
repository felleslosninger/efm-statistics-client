package no.digdir.efmstatisticsclient.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "digdir.statisticsclient")
@Data
public class StatisticsClientProperties {

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

}



