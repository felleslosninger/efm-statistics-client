package no.digdir.efmstatisticsclient.domain;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//import org.jetbrains.annotations.NotNull;
//import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "status")
@Data
public class StatusProperties {
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

    public StatusProperties() {
    }
}



