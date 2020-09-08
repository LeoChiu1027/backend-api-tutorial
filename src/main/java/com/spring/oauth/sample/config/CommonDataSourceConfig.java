package com.spring.oauth.sample.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class CommonDataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(CommonDataSourceConfig.class);

    public CommonDataSourceConfig() {
    }

    @Bean
    @Primary
    @ConfigurationProperties(
            prefix = "spring.datasource"
    )
    public DataSource primaryDatasource() {
        return DataSourceBuilder.create().build();
    }
}
