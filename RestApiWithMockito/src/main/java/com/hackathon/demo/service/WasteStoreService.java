package com.hackathon.demo.service;

import com.hackathon.demo.entity.WasteData;
import com.hackathon.demo.model.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component("WasteStoreService")
public class WasteStoreService extends WasteService {



    @Override
    public List<WasteData> getWasteDetails(UserInput userInput) {
        List<WasteData> results = new ArrayList<>();
        int vendorId = userInput.getVendorNumber();
        try {

            Connection connection = verticaDataSource.getConnection();
            PreparedStatement ps = connection
                    .prepareStatement(" select distinct  state,city, zip, county,district from RDP_WALGREENS_PRADEEP.olap_Store Os inner join  RDP_WALGREENS_PRADEEP.RAW_WASTE_DATA RWD on CAST(RWD.store_id AS VARCHAR(20)) = OS.store_id and RWD.whse_prod_vendor_number = "+vendorId);
            ResultSet res = ps.executeQuery();
            WasteData waste;
            while (res.next()) {
                waste = new WasteData();
                waste.setState(res.getString("state"));
                waste.setCity(res.getString("city"));
                System.out.println(" zip " + res.getString("zip"));
                waste.setZip(res.getInt("zip"));
                waste.setCounty(res.getString("county"));
                waste.setDistrict(res.getString("district"));
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
