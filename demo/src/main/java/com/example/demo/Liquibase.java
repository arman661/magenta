package com.example.demo;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

public class Liquibase {
    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db.changelog-master.xml");
        liquibase.setDataSource(dataSource());
        return liquibase;
    }

    @ConfigurationProperties(prefix = "datasource.mysql")
    @Bean
    @Primary
    private DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }
}
