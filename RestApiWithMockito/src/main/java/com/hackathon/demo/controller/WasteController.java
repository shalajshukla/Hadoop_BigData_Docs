package com.hackathon.demo.controller;

import com.hackathon.demo.entity.UserDetails;
import com.hackathon.demo.entity.WasteData;
import com.hackathon.demo.factory.WasteServiceFactory;
import com.hackathon.demo.factory.WasteType;
import com.hackathon.demo.model.UserInput;
import com.hackathon.demo.service.WasteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class WasteController {


	Logger logger = LoggerFactory.getLogger(WasteController.class);


	@Autowired
	private WasteServiceFactory wasteServiceFactory;


	@PostMapping(value="/wasteDetails",produces = "application/json")
	public List<WasteData> getWasteDetails(@RequestBody UserInput userInput) {
		System.out.println(" get request for Reason Data");
		WasteType wasteType = WasteType.valueOf(userInput.getType());
		System.out.println(" waste Type "+wasteType + " wasteServiceFactory " +wasteServiceFactory);
		WasteService wasteService = wasteServiceFactory.getWasteService(wasteType);
		System.out.println(" wasteserive in controller " +wasteService);
		List<WasteData> value = wasteService.getWasteDetails(userInput);
		return value;
	}


}
