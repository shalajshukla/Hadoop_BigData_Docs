package com.hackathon.demo.service;

import com.hackathon.demo.entity.WasteData;
import com.hackathon.demo.model.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component("WasteReasonService")
public class WasteReasonService extends WasteService {


    @Override
    public List<WasteData> getWasteDetails(UserInput userInput) {
        int vendorId = userInput.getVendorNumber();
        int startPeriodKey =  userInput.getStartPeriodKey();
        int endPeriodKey = userInput.getEndPeriodKey();
        List<WasteData> results = new ArrayList<>();
        try {

            Connection connection = verticaDataSource.getConnection();
            String sql = "select claim_reason_name, sum(ext_cost_dlrs) as Cost from  RDP_WALGREENS_PRADEEP.RAW_WASTE_DATA where whse_prod_vendor_number = "+vendorId+" and period_key between "+startPeriodKey+"  and "+endPeriodKey+" group by claim_reason_name";
            logger.info(sql);
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet res = ps.executeQuery();
            WasteData waste;
            logger.info( " vendor id "+ vendorId + " "+startPeriodKey+ " "+endPeriodKey);
            while (res.next()) {
                logger.info( " result set ");
                waste = new WasteData();
                waste.setClaimReasonName(res.getString("claim_reason_name"));
                waste.setExtnCostDollar(res.getDouble("Cost"));

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
