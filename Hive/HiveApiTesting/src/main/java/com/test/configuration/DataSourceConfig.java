package com.test.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${hiveuri}")
    private String hiveDatabaseUri;

    @Value("${hiveusername}")
    private String hiveUsername;

    @Value("${hivepassword}")
    private String hivePassword;


    @Bean
    public DataSource hiveDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(hiveDatabaseUri);
        dataSource.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        dataSource.setUsername(hiveUsername);
        dataSource.setPassword(hivePassword);

        return dataSource;
    }

}

