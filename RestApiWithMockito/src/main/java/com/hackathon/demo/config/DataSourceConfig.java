package com.hackathon.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${verticaUri}")
    private String verticaDatabaseUri;

    @Value("${verticaUserName}")
    private String verticaUsername;

    @Value("${verticaPassword}")
    private String verticaPassword;

    @Bean
    public DataSource verticaDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(verticaDatabaseUri);
        dataSource.setDriverClassName("com.vertica.jdbc.DataSource");
        dataSource.setUsername(verticaUsername);
        dataSource.setPassword(verticaPassword);

        return dataSource;
    }
}
