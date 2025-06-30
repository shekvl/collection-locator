package com.anonymizerweb.anonymizerweb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.anonymizerweb.anonymizerweb.repositories.cdm",
        entityManagerFactoryRef = "cdmEntityManager",
        transactionManagerRef = "cdmTransactionManager")
public class PersistenceCdmAutoConfiguration {

    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix="spring.cdm-datasource")
    public DataSource cdmDataSource() {
        return DataSourceBuilder.create().build();
    }
    // userEntityManager bean
    @Bean
    public LocalContainerEntityManagerFactoryBean cdmEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cdmDataSource());
        em.setPackagesToScan(
                new String[] { "com.anonymizerweb.anonymizerweb.entities.cdm" });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    // userTransactionManager bean
    @Bean
    public PlatformTransactionManager cdmTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                cdmEntityManager().getObject());
        return transactionManager;
    }
}
