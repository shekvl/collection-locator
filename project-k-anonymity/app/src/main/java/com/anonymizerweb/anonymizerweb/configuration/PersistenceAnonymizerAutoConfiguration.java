package com.anonymizerweb.anonymizerweb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.anonymizerweb.anonymizerweb.repositories.anonymizer",
        entityManagerFactoryRef = "anonymizerEntityManager",
        transactionManagerRef = "anonymizerTransactionManager")
public class PersistenceAnonymizerAutoConfiguration {

    @Autowired
    private Environment env;

    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource anonymizerDataSource() {
        return DataSourceBuilder.create().build();
    }
    // userEntityManager bean
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean anonymizerEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(anonymizerDataSource());
        em.setPackagesToScan(
                new String[] { "com.anonymizerweb.anonymizerweb.entities.anonymizer" });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        properties.put("hibernate.physical_naming_strategy", "com.anonymizerweb.anonymizerweb.entities.LocalPhysNamingStrategy");
        em.setJpaPropertyMap(properties);

        return em;
    }

    // userTransactionManager bean
    @Primary
    @Bean
    public PlatformTransactionManager anonymizerTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                anonymizerEntityManager().getObject());
        return transactionManager;
    }
}
