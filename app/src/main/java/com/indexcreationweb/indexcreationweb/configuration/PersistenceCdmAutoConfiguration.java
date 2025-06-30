package com.indexcreationweb.indexcreationweb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = "com.indexcreationweb.indexcreationweb.repositories.cdm",
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
                new String[] { "com.indexcreationweb.indexcreationweb.entities.cdm" });

        HibernateJpaVendorAdapter vendorAdapter
                = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
                env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
                env.getProperty("hibernate.dialect"));

//        properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
//        properties.put("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql", "false"));
//        properties.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql", "false"));
//        properties.put("hibernate.use_sql_comments", env.getProperty("spring.jpa.properties.hibernate.use_sql_comments", "false"));
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
