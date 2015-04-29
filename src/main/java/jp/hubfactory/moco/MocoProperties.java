package jp.hubfactory.moco;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "moco", ignoreUnknownFields = true)
public class MocoProperties {

    private final System system = new System();

    public System getSystem() {
        return system;
    }

    @Data
    public static class System {
        private String itunes;
        private String itunesSandbox;
    }
}
