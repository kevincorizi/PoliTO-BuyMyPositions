package it.polito.gruppo2.mamk.lab3.datasources;

import com.mongodb.MongoClient;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.sql.DataSource;


@Configuration
@PropertySource("classpath:db.properties") // to be moved elsewhere
@EnableMongoRepositories(basePackages = "it.polito.gruppo2.mamk.lab3.persistence")
public class DataSourcesConfiguration {

    @Autowired
    private Environment env;

    @Bean
    @Qualifier("clientDataSource") // Qualifiers allow to autowire specific beans
    public DataSource clientsDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(env.getProperty("db.driver"));
        ds.setUrl(env.getProperty("clientdb.url"));
        ds.setUsername(env.getProperty("db.username"));
        ds.setPassword(env.getProperty("db.password"));
        return ds;
    }

    @Bean
    @Qualifier("userDataSource")
    public DataSource usersDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(env.getProperty("db.driver"));
        ds.setUrl(env.getProperty("db.url"));
        ds.setUsername(env.getProperty("db.username"));
        ds.setPassword(env.getProperty("db.password"));
        return ds;
    }

    @Bean
    public MongoClient mongo() {
        return new MongoClient(env.getProperty("mongo.url"), Integer.parseInt(env.getProperty("mongo.port")));
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), env.getProperty("mongo.dbname"));
    }

}
