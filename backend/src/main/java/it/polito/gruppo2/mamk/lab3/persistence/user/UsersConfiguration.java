package it.polito.gruppo2.mamk.lab3.persistence.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

// Class configuring JPA repository for users data ( or we could use it for the whole relational part)

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "usersEntityManagerFactory",
        transactionManagerRef = "usersTransactionManager")
public class UsersConfiguration {
    @Autowired
    @Qualifier("userDataSource")
    private DataSource dataSource;

    @Bean
    public LocalContainerEntityManagerFactoryBean usersEntityManagerFactory() {
        // Used to locate and configure the JPA implementation to be used.
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
        jpaVendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        Properties jpaProperties = new Properties();
        // Solves an exception thrown by Hibernate because the method createClob() is not implemented.
        jpaProperties.put("hibernate.jdbc.lob.non_contextual_creation", "true");

        factoryBean.setJpaProperties(jpaProperties);
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        // Which packages to scan searching for entities
        factoryBean.setPackagesToScan(UsersConfiguration.class.getPackage().getName());

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager usersTransactionManager() {
        return new JpaTransactionManager(usersEntityManagerFactory().getObject());
    }

}
