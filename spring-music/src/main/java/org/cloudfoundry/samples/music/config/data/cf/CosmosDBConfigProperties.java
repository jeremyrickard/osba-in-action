package org.cloudfoundry.samples.music.config.data.cf;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cosmosdb")
@ConfigurationProperties(prefix="vcap.services.cosmosdb.credentials")
@ConditionalOnProperty(value="runtime", havingValue="cf", matchIfMissing=true)
public class CosmosDBConfigProperties {

    @PostConstruct
    public void init(){ 
    }
    
    private String connectionString;

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getConnectionString() {
        return this.connectionString;
    }
}