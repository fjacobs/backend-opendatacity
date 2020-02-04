package com.dynacore.livemap.block.guidancesign;

import com.dynacore.livemap.core.model.FeatureCollection;
import com.dynacore.livemap.block.guidancesign.model.GuidanceSignModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Profile("guidancesign")
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
