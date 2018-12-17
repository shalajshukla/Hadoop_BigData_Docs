package com.hackathon.demo;


import com.hackathon.demo.controller.WasteController;
import com.hackathon.demo.entity.WasteData;
import com.hackathon.demo.factory.WasteServiceFactory;
import com.hackathon.demo.factory.WasteType;
import com.hackathon.demo.model.UserInput;
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

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(value = WasteController.class, secure = false)
public class WasteControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private WasteServiceFactory wasteServiceFactory;

    @MockBean
    private WasteStoreService wasteStoreService;

    @Autowired
    private WasteController wasteController;

    /*@InjectMocks
    private final WasteStoreService wasteStoreService = new WasteStoreService();
*/

    /*@Test
    public void callRealMethodFromMock () {
        System.out.println(" wasteServiceFactory  " +wasteServiceFactory);
        WasteType wasteType = WasteType.valueOf("Store");
        Mockito.when( wasteServiceFactory.myMessage() ).thenCallRealMethod();
        wasteServiceFactory.myMessage();

    }*/

    @Test
    public void getStoreDropDownValuesTest() throws Exception {

        WasteData wasteData1 = new WasteData();
        wasteData1.setState("WI");
        wasteData1.setCity("SHOREWOOD,WI");
        wasteData1.setZip(53211);
        wasteData1.setCounty("MILWAUKEE");
        wasteData1.setDistrict("MILWAUKEE NORTHEAST");


        List<WasteData> wasteDataList = Arrays.asList(wasteData1);


        String userInputJson = "{\"vendorNumber\":1,\"startPeriodKey\":20170909,\"endPeriodKey\":20171008,\"type\":\"Store\"}";


        // since wasteController getWasteDetail internally using wasteServiceFactory as autowire bean , we need to mock
        // this method else we would get null pointer exception as autowire bean doesn't get instantiate via mocking
        Mockito.when(wasteServiceFactory.getWasteService(Mockito.any(WasteType.class))).thenReturn(wasteStoreService);

        //Mockito.when(wasteController.getWasteDetails(Mockito.any(UserInput.class))).thenCallRealMethod();
        Mockito.when(wasteStoreService.getWasteDetails(Mockito.any(UserInput.class))).thenReturn(wasteDataList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/wasteDetails").accept(MediaType.APPLICATION_JSON).content(userInputJson).contentType(MediaType.APPLICATION_JSON);;

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(" response " + result.getResponse().getContentAsString());
        String expected = "[{state:WI,city:\"SHOREWOOD,WI\",zip:53211,county:MILWAUKEE,district:\"MILWAUKEE NORTHEAST\"}]";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),false);
    }
}
