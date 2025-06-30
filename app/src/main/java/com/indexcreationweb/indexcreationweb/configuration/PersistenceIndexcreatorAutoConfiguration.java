package com.indexcreationweb.indexcreationweb.configuration;

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
        basePackages = "com.indexcreationweb.indexcreationweb.repositories.indexcreator",
        entityManagerFactoryRef = "indexcreatorEntityManager",
        transactionManagerRef = "indexcreatorTransactionManager")
public class PersistenceIndexcreatorAutoConfiguration {

    @Autowired
    private Environment env;

    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource indexcreatorDataSource() {
        return DataSourceBuilder.create().build();
    }
    // userEntityManager bean
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean indexcreatorEntityManager() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(indexcreatorDataSource());
        em.setPackagesToScan(
                new String[] { "com.indexcreationweb.indexcreationweb.entities.indexcreator" });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));
        properties.put("hibernate.physical_naming_strategy", "com.indexcreationweb.indexcreationweb.entities.LocalPhysNamingStrategy");
        em.setJpaPropertyMap(properties);

        return em;
    }

    // userTransactionManager bean
    @Primary
    @Bean
    public PlatformTransactionManager indexcreatorTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                indexcreatorEntityManager().getObject());
        return transactionManager;
    }
}
