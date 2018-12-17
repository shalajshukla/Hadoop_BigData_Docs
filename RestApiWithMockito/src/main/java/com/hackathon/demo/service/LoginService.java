package com.hackathon.demo.service;

import com.hackathon.demo.entity.UserDetails;
import com.hackathon.demo.entity.WasteData;
import com.hackathon.demo.model.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component("LoginService")
public class LoginService {


	Logger logger = LoggerFactory.getLogger(LoginService.class);


	@Autowired
	public DataSource verticaDataSource;




	public  List<UserDetails> getUserDetails (){
		List<UserDetails> results = new ArrayList<>();
		try {

			Connection connection = verticaDataSource.getConnection();
			PreparedStatement ps = connection
					.prepareStatement("select vendorId, UserName from  RDP_WALGREENS_PRADEEP.User_Details");
			ResultSet res = ps.executeQuery();
			UserDetails userDetails;
			while (res.next()) {
				userDetails = new UserDetails();
				userDetails.setVendorId(res.getInt("vendorId"));
				userDetails.setUserName(res.getString("UserName"));

				results.add(userDetails);
			}

			res.close();
			ps.close();
			connection.close();

			logger.info("Size=" + results.size());
		} catch (Exception e) {
			logger.error("Error in search", e);
		}

		return results;
	}



}
