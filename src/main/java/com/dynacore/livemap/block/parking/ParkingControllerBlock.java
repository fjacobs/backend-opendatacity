package com.dynacore.livemap.block.parking;

import com.dynacore.livemap.block.core.model.FeatureCollectionBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Profile("parking")
@Controller
public class ParkingControllerBlock {

  private ParkingService parkingPlaceService;
  private final Logger logger = LoggerFactory.getLogger(ParkingControllerBlock.class);

  @Autowired
  public ParkingControllerBlock(ParkingService parkingPlaceService) {
    this.parkingPlaceService = parkingPlaceService;
  }

  @CrossOrigin
  @GetMapping(value = "getCustomParkingJson")
  @ResponseBody
  public FeatureCollectionBlock<ParkingModel> getCustomParkingJson() {
    return parkingPlaceService.getLastUpdate();
  }
}
