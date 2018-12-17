package com.hackathon.demo.service;

import com.hackathon.demo.entity.Demo;
import com.hackathon.demo.entity.Waste;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component("DataSourceService")
public class DataSourceService {


	Logger logger = LoggerFactory.getLogger(DataSourceService.class);

	@Qualifier("hive")
	@Autowired
	public DataSource hiveDataSource;

	@Qualifier("vertica")
	@Autowired
	public DataSource verticaDataSource;


	public List<Demo> getDemoData() {

		List<Demo> results = new ArrayList<>();
		try {

			Connection connection = hiveDataSource.getConnection();
			PreparedStatement ps = connection
					.prepareStatement("select * from demo");
			ResultSet res = ps.executeQuery();
			Demo demo = null;
			while (res.next()) {
				demo = new Demo();
				demo.setKey(res.getInt("key"));
				demo.setValue(res.getString("value"));
				results.add(demo);
			}

			res.close();
			ps.close();
			connection.close();

			logger.error("Size=" + results.size());
		} catch (Exception e) {
			logger.error("Error in search", e);
		}

		return results;
	}

	public List<Waste> getWasteDataFromVertica() {

		List<Waste> results = new ArrayList<>();
		try {

			Connection connection = verticaDataSource.getConnection();
			PreparedStatement ps = connection
					.prepareStatement("select claim_reason_name,period_key,store_id,prod_id from RDP_WALGREENS_PRADEEP.RAW_WASTE_DATA limit 10");
			ResultSet res = ps.executeQuery();
			Waste waste;
			while (res.next()) {
				waste = new Waste();
				waste.setClaimReasonName(res.getString("claim_reason_name"));
				waste.setPeriodKey(res.getString("period_key"));
				waste.setProdId(res.getString("prod_id"));
				waste.setStoreId(res.getString("store_id"));
				results.add(waste);
			}

			res.close();
			ps.close();
			connection.close();

			logger.error("Size=" + results.size());
		} catch (Exception e) {
			logger.error("Error in search", e);
		}

		return results;
	}

}
