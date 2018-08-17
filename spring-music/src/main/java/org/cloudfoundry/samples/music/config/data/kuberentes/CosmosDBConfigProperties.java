package org.cloudfoundry.samples.music.config.data.kuberentes;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cosmosdb")
@ConfigurationProperties(prefix="cosmosdb")
@ConditionalOnProperty(value="runtime", havingValue="kubernetes", matchIfMissing=true)
public class CosmosDBConfigProperties {

    
    private String uri;

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getURI() {
        return this.uri;
    }
}