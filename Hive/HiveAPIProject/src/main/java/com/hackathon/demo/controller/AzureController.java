package com.hackathon.demo.controller;

import com.hackathon.demo.model.AzureResponse;
import com.hackathon.demo.service.AzureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class AzureController {

    Logger logger = LoggerFactory.getLogger(AzureController.class);

    @Autowired
    private AzureService azureService;



    @PostMapping(value="/upload" ,produces = "application/json")
    public AzureResponse uploadToAzure(@RequestBody String fileName) {
        logger.info(" get request for upload");

        AzureResponse azureResponse = new AzureResponse();
        azureResponse.setStatus("Failure");
        try {
           // azureResponse.setStatus(azureService.uploadToAzure(fileName,"wasteData") == 0 ? "Success" : "Failure");
            if(azureService.uploadToAzure(fileName,"wasteData") == 0) {
                //  azureResponse = sparkSubmitCall(fileName)
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return azureResponse;
    }

    @PostMapping(value="/submitJob" ,produces = "application/json")
    public AzureResponse sparkSubmitCall() {
        logger.info(" get request for spark submit");
        String status = "in-progress";

        AzureResponse azureResponse = new AzureResponse();
        azureResponse.setStatus("Failure");
        try {
            long jobId = azureService.sparkSubmit();
            if (jobId != -1) {
                do {
                    status = azureService.getJobStatus(jobId);
                    if("success".equals(status)) {
                        azureResponse.setStatus("Success");
                        break;
                    } else if ("failure".equals(status)){
                        break;
                    }
                    Thread.sleep(1000*5);
                } while ("in-progress".equals(status));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return azureResponse;
    }


}

