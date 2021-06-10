package com.test.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class HiveService {

    @Autowired
    public DataSource hiveDataSource;


    public void getDemoData() {

        try (
                Connection connection = hiveDataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement("select * from test_shalaj.customer");
                ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                System.out.println("Id=" + rs.getString(1));
                System.out.println("Name=" + rs.getString(2));
                System.out.println("Address=" + rs.getString(3));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
