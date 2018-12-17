package com.hackathon.demo.factory;

import com.hackathon.demo.service.WasteReasonService;
import com.hackathon.demo.service.WasteService;
import com.hackathon.demo.service.WasteStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("WasteServiceFactory")
public class WasteServiceFactory {

    @Autowired
    private WasteReasonService wasteReasonService;
    @Autowired
    private WasteStoreService wasteStoreService;


    public WasteService getWasteService(WasteType type) {
        WasteService wasteService = null;
        switch (type) {
            case Reason:
                wasteService = wasteReasonService;
                break;
            case Store:
                wasteService = wasteStoreService;
                break;
            default:
                System.out.println(" invalid type");
        }
        return wasteService;
    }
}
