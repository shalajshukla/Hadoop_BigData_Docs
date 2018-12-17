package com.hackathon.demo.config;

import org.apache.commons.dbcp.BasicDataSource;
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

    @Value("${verticaUri}")
    private String verticaDatabaseUri;

    @Value("${verticaUserName}")
    private String verticaUsername;

    @Value("${verticaPassword}")
    private String verticaPassword;

    @Bean (name="hive")
    public DataSource hiveDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(hiveDatabaseUri);
        dataSource.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        dataSource.setUsername(hiveUsername);
        dataSource.setPassword(hivePassword);

        return dataSource;
    }
    @Bean (name="vertica")
    public DataSource verticaDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(verticaDatabaseUri);
        dataSource.setDriverClassName("com.vertica.jdbc.DataSource");
        dataSource.setUsername(verticaUsername);
        dataSource.setPassword(verticaPassword);

        return dataSource;
    }
}
