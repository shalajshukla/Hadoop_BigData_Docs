package com.hackathon.demo;

import java.util.Arrays;
import java.util.List;

import com.hackathon.demo.controller.LoginController;
import com.hackathon.demo.controller.WasteController;
import com.hackathon.demo.entity.UserDetails;
import com.hackathon.demo.entity.WasteData;
import com.hackathon.demo.factory.WasteServiceFactory;
import com.hackathon.demo.factory.WasteType;
import com.hackathon.demo.model.UserInput;
import com.hackathon.demo.service.LoginService;
import com.hackathon.demo.service.WasteService;
import com.hackathon.demo.service.WasteStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringRunner.class)
@WebMvcTest(value = LoginController.class, secure = false)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @MockBean
    private WasteServiceFactory wasteServiceFactory;



    @Test
    public void getUserDetailsTest() throws Exception {
        List<UserDetails> userDetailList = Arrays.asList(new UserDetails(55, "Walgreens"));

        Mockito.when(loginService.getUserDetails()).thenReturn(userDetailList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/loginData").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(" response " + result.getResponse().getContentAsString());
        String expected = "[{vendorId:55,userName:Walgreens}]";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),false);
    }
   /* @Test
    public void getReasonCostTest() throws Exception {

        WasteData wasteData1 = new WasteData();
        wasteData1.setClaimReasonName("OUTDATED");
        wasteData1.setExtnCostDollar(198.99);

        WasteData wasteData2 = new WasteData();
        wasteData2.setClaimReasonName("SHELF DAMAGE");
        wasteData2.setExtnCostDollar(244.85);


        List<WasteData> wasteDataList = Arrays.asList(wasteData1,wasteData2);


        String userInputJson = "{\"vendorNumber\":1,\"startPeriodKey\":20170909,\"endPeriodKey\":20171008}";

        Mockito.when(loginService.getReasonCost(Mockito.any(UserInput.class))).thenReturn(wasteDataList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/reasonData").accept(MediaType.APPLICATION_JSON).content(userInputJson).contentType(MediaType.APPLICATION_JSON);;

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(" response " + result.getResponse().getContentAsString());
        String expected = "[{claimReasonName:OUTDATED,extnCostDollar:198.99},{claimReasonName:\"SHELF DAMAGE\",extnCostDollar:244.85}]";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),false);
    }*/



   /* @Test
    public void getProductDropDownValuesTest() throws Exception {

        WasteData wasteData1 = new WasteData();
        wasteData1.setProdCatName("COFFEE MULTI PACK197129");

        List<WasteData> wasteDataList = Arrays.asList(wasteData1);

        String userInputJson = "{\"vendorNumber\":1,\"startPeriodKey\":20170909,\"endPeriodKey\":20171008}";

        Mockito.when(dataSourceService.getProductDropDownValues(Mockito.any(UserInput.class))).thenReturn(wasteDataList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/productDropDownValues").accept(MediaType.APPLICATION_JSON).content(userInputJson).contentType(MediaType.APPLICATION_JSON);;

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(" response " + result.getResponse().getContentAsString());
        String expected = "[{\"prodCatName\":\"COFFEE MULTI PACK197129\"}]";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),false);
    }*/



}
