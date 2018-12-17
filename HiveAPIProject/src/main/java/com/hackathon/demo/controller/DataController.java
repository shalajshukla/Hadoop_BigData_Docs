package com.hackathon.demo.controller;

import com.hackathon.demo.entity.Demo;
import com.hackathon.demo.entity.Waste;
import com.hackathon.demo.service.AzureService;
import com.hackathon.demo.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author tspann
 *
 */
@RestController
public class DataController {


	Logger logger = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private DataSourceService dataSourceService;


	@PostMapping(value="/demo",produces = "application/json")
	public List<Demo> getData() {
		logger.info(" get request for demo");
		List<Demo> value = dataSourceService.getDemoData();
		return value;
	}

	@PostMapping(value="/waste",produces = "application/json")
	public List<Waste> getWasteDataFromVertica() {
		logger.info(" get request for waste");
		List<Waste> value = dataSourceService.getWasteDataFromVertica();
		return value;
	}



}
