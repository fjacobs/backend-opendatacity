package com.dynacore.livemap.guidancesign;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.guidancesign.model.GuidanceSignModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GuidanceSignController {

    private final GuidanceSignService guidanceSignService;

    @Autowired
    public GuidanceSignController(GuidanceSignService guidanceSignService) {
        this.guidanceSignService = guidanceSignService;
    }

    @GetMapping(value = "getCustomGuidanceSignJson")
    @ResponseBody
    public FeatureCollection<GuidanceSignModel> getCustomJson() {
        return guidanceSignService.getLastUpdate();
    }
}
