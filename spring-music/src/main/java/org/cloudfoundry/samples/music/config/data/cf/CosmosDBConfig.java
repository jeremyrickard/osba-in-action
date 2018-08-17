package org.cloudfoundry.samples.music.config.data.cf;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
@Profile("cosmosdb")
@ConditionalOnProperty(value="runtime", havingValue="cf", matchIfMissing=true)
public class CosmosDBConfig {

    @Autowired 
    CosmosDBConfigProperties config;

    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(new MongoClient(new MongoClientURI(config.getConnectionString())), "music");
    }
    
}
