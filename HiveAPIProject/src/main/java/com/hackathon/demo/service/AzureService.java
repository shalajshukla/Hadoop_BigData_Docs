package com.hackathon.demo.service;

import com.hackathon.demo.controller.AzureController;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component("AzureService")
public class AzureService {

    final static String servicePrincipal = "https://poc_principal.azurehdinsight.net/2018-08-10T07:38:15.638Z";
    final static String passPhrase = "OJ6P8HUD86f8c+fT4tW+3iwPcZG0YXTXsdqQWnMKAP4=";
    final static String tenantId = "c9547909-d60c-4b1f-8a84-78138eacf3f4";
    final static String blobContainer = "demo";
    final static  String blobAccountName = "poccisdata";
    final static  String blobAccountKey = "w/M25nOe/PLGenlNdkAwKy6qEM7yx19zb7Haql5cQEi/CMW2+JDR4TIyxt5YTtGGVFMxTyda0e2UHH8q6e1sGQ==";

    Logger logger = LoggerFactory.getLogger(AzureService.class);

    @Autowired
    private RestTemplate restTemplate;

    public int uploadToAzure (String src,String dest) throws IOException,InterruptedException {


        List<String> commands = new ArrayList<>();
        //commands.add("az");
        commands.add("C:\\Program Files (x86)\\Microsoft SDKs\\Azure\\CLI2\\wbin\\az.cmd");
        commands.add("login");
        commands.add("--service-principal");
        commands.add("-u");
        commands.add(servicePrincipal);
        commands.add("-p");
        commands.add(passPhrase);
        commands.add("--tenant");
        commands.add(tenantId);
        // commands.add("--allow-no-subscriptions");


        ProcessBuilder pb = new ProcessBuilder(commands);

        pb.redirectErrorStream(true);
        Process process = pb.start();

        //Read output
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line, previous = null;
        while ((line = br.readLine()) != null)
            if (!line.equals(previous)) {
                previous = line;
                out.append(line).append('\n');
                logger.info(""+line);
            }
        //Check result
        if (process.waitFor() == 0) {
            logger.info("Successfully login to Azure");
        } else {
            logger.error("Error occur while login to Azure");
            return 1;
        }
        //#############################  FILE UPLOAD COMMAND    ######################
        logger.info(" source filePath "+src);
        String fileName = Paths.get(src).getFileName().toString();

        logger.info(" source filename "+fileName);
        String destination = dest;

        if(destination.startsWith("/")) {
            destination = destination.substring(destination.indexOf("/") + 1);
        }
        if (!destination.endsWith("/")) {
            destination = destination +"/";
        }

        commands = new ArrayList<>();
        //commands.add("az");
        commands.add("C:\\Program Files (x86)\\Microsoft SDKs\\Azure\\CLI2\\wbin\\az.cmd");
        commands.add("storage");
        commands.add("blob");
        commands.add("upload");
        commands.add("--container-name");
        commands.add(blobContainer);
        commands.add("--file");
        commands.add(src);
        commands.add("--name");
        commands.add(destination+fileName);


        pb = new ProcessBuilder(commands);
        pb.environment().put("AZURE_STORAGE_ACCOUNT", blobAccountName);
        pb.environment().put("AZURE_STORAGE_KEY", blobAccountKey);
        logger.info("Upload Command ="+commands);

        pb.redirectErrorStream(true);
        process = pb.start();

        //Read output
        out = new StringBuilder();
        br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        previous = null;
        while ((line = br.readLine()) != null)
            if (!line.equals(previous)) {
                previous = line;
                out.append(line).append('\n');
                logger.info(""+line);
            }
        //Check result
        if (process.waitFor() == 0) {
            logger.info("File uploaded successfully ");
            return 0;
        } else {
            logger.error("Error occur while uploading file to azure ");
        }
        return 1;
    }

    public long sparkSubmit () throws Exception{

        Long jobId = -1L;
        String urlString ="https://poccluster.azurehdinsight.net/livy/batches";

        String base64Creds = getBase64Key("admin","RSi$123456");


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        JSONObject request = new JSONObject();
        request.put("file","wasb://demo@poccisdata.blob.core.windows.net/sample/sparksample_2.11-0.1.jar");
        request.put("className", "com.sample.SparkSample");

        JSONArray array = new JSONArray();
        //array.put("wasb://demo@poccisdata.blob.core.windows.net/tmp/20180322/Sales/47002000000462/Query/Query-6040009.json");

        request.put("args",array);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);


        ResponseEntity<String> response =restTemplate.exchange(urlString, HttpMethod.POST, entity, String.class);
        logger.info(" fetching response code "+response.getStatusCode());
        if (response.getStatusCode() == HttpStatus.CREATED) {

            JSONObject responseJson = new JSONObject(response.getBody());
            jobId = responseJson.getLong("id");
            logger.info(responseJson.toString());
        }

        return jobId;

    }

    public String getJobStatus(long jobId) throws Exception {
        String status = "in-progress";
        logger.info("Looking up " + jobId);
        String url = String.format("https://poccluster.azurehdinsight.net/livy/batches/%s", jobId);
        String base64Creds = getBase64Key("admin","RSi$123456");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        logger.info("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody());

        JSONObject responseJson = new JSONObject(response.getBody());
        String state = responseJson.getString("state");
       logger.info( " state of spark job "+ state);
        switch (state) {
            case "success":
                status="success";
                logger.info(" succeeded spark job sending response back");
                break;
            case "dead":
            case "error":
                status="failure";
                break;
        }

        return status;
    }

    public static String getBase64Key(String userName,String password) throws UnsupportedEncodingException {
        String base64key = null;
        try {
            base64key = Base64.encodeBase64String((userName+":"+password).getBytes("UTF-8"));
        } catch(UnsupportedEncodingException e) {
            throw e;
        }
        return base64key;
    }
}
