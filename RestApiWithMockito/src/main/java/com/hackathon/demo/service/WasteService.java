package com.hackathon.demo.service;

import com.hackathon.demo.entity.WasteData;
import com.hackathon.demo.model.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.List;


public abstract class WasteService {

    @Autowired
    public DataSource verticaDataSource;

    Logger logger = LoggerFactory.getLogger(WasteService.class);
    public abstract List<WasteData> getWasteDetails(UserInput userInput);
}
