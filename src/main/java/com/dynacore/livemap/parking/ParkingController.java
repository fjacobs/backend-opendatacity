package com.dynacore.livemap.parking;

import com.dynacore.livemap.common.model.FeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ParkingController {

    private ParkingService parkingPlaceService;
    private final Logger logger = LoggerFactory.getLogger(ParkingController.class);


    @Autowired
    public ParkingController(ParkingService parkingPlaceService) {
        this.parkingPlaceService = parkingPlaceService;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping(value = "getCustomParkingJson")
    @ResponseBody
    public FeatureCollection<ParkingModel> getCustomParkingJson() {
        logger.info("getCustomParkingJson called....");
        return parkingPlaceService.getLiveData();
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping(value = "frontEndTest")
    @ResponseBody
    public String frontEndTest() {
        logger.info("front called....");
        return new String("{\n" +
                "\"type\": \"FeatureCollection\",\n" +
                "\"features\": [\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P12 Markenhoven\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.908583,52.369804] }, \n" +
                " \"properties\": {\"Name\":\"CE-P12 Markenhoven\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"34\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"127\",\"LongCapacity\":\"49\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P23 Bijlmerdreef\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.955472,52.31764] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P23 Bijlmerdreef\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"158\",\"FreeSpaceLong\":\"136\",\"ShortCapacity\":\"250\",\"LongCapacity\":\"150\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P05 Villa ArenA\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.938667,52.312] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P05 Villa ArenA\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"506\",\"FreeSpaceLong\":\"67\",\"ShortCapacity\":\"780\",\"LongCapacity\":\"200\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P09 Bijenkorf\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.8950834,52.373833] }, \n" +
                " \"properties\": {\"Name\":\"CE-P09 Bijenkorf\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"11\",\"FreeSpaceLong\":\"16\",\"ShortCapacity\":\"456\",\"LongCapacity\":\"25\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P04 Amsterdam Centraal\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.897222,52.37908] }, \n" +
                " \"properties\": {\"Name\":\"CE-P04 Amsterdam Centraal\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"86\",\"FreeSpaceLong\":\"76\",\"ShortCapacity\":\"381\",\"LongCapacity\":\"96\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P22 Bijlmerdreef\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9526668,52.31475] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P22 Bijlmerdreef\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"81\",\"FreeSpaceLong\":\"134\",\"ShortCapacity\":\"225\",\"LongCapacity\":\"175\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P11 Waterlooplein\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9043612,52.36922] }, \n" +
                " \"properties\": {\"Name\":\"CE-P11 Waterlooplein\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"16\",\"FreeSpaceLong\":\"12\",\"ShortCapacity\":\"100\",\"LongCapacity\":\"40\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P21 Bijlmerdreef\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9505835,52.31414] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P21 Bijlmerdreef\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"47\",\"FreeSpaceLong\":\"85\",\"ShortCapacity\":\"113\",\"LongCapacity\":\"130\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P24 Bijlmerdreef\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9548054,52.314777] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P24 Bijlmerdreef\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"168\",\"FreeSpaceLong\":\"55\",\"ShortCapacity\":\"515\",\"LongCapacity\":\"75\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P04 Villa ArenA\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.938667,52.313] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P04 Villa ArenA\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"973\",\"FreeSpaceLong\":\"5\",\"ShortCapacity\":\"1090\",\"LongCapacity\":\"5\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZD-P3 VU campus\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.864,52.333805] }, \n" +
                " \"properties\": {\"Name\":\"ZD-P3 VU campus\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"78\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"83\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZD-P2 VUmc (ACTA)\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.860889,52.336193] }, \n" +
                " \"properties\": {\"Name\":\"ZD-P2 VUmc (ACTA)\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"342\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"447\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZD-P1 VUmc (westflank)\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.8595,52.334] }, \n" +
                " \"properties\": {\"Name\":\"ZD-P1 VUmc (westflank)\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"307\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"413\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE P+R Zeeburg 2\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9644723,52.36989] }, \n" +
                " \"properties\": {\"Name\":\"CE P+R Zeeburg 2\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"11\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"400\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P08 De Kolk\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.894583,52.37639] }, \n" +
                " \"properties\": {\"Name\":\"CE-P08 De Kolk\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"101\",\"FreeSpaceLong\":\"-10\",\"ShortCapacity\":\"300\",\"LongCapacity\":\"75\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE P+R Zeeburg 1\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.960417,52.37136] }, \n" +
                " \"properties\": {\"Name\":\"CE P+R Zeeburg 1\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"1\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"210\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P03 Piet Hein\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.915389,52.377888] }, \n" +
                " \"properties\": {\"Name\":\"CE-P03 Piet Hein\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"295\",\"FreeSpaceLong\":\"184\",\"ShortCapacity\":\"350\",\"LongCapacity\":\"200\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P01 ArenA / P+R\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.941056,52.314083] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P01 ArenA / P+R\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"288\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"1000\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P01 ArenA\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.943,52.314083] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P01 ArenA\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"285\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"285\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P18 HES/ROC\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9473057,52.315224] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P18 HES/ROC\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"14\",\"FreeSpaceLong\":\"227\",\"ShortCapacity\":\"38\",\"LongCapacity\":\"250\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE P+R Bos en Lommer\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.845361,52.379333] }, \n" +
                " \"properties\": {\"Name\":\"CE P+R Bos en Lommer\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"0\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"250\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P07 Museumplein\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.8794446,52.35725] }, \n" +
                " \"properties\": {\"Name\":\"CE-P07 Museumplein\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"116\",\"FreeSpaceLong\":\"163\",\"ShortCapacity\":\"449\",\"LongCapacity\":\"198\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P Olympisch Stadion\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.8,52.3] }, \n" +
                " \"properties\": {\"Name\":\"CE-P Olympisch Stadion\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"error\",\"FreeSpaceShort\":\"0\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"644\",\"LongCapacity\":\"200\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P02 P+R Olympisch stadion\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.853611,52.343777] }, \n" +
                " \"properties\": {\"Name\":\"CE-P02 P+R Olympisch stadion\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"0\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"350\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P14 Westerpark\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.8662777,52.38461] }, \n" +
                " \"properties\": {\"Name\":\"CE-P14 Westerpark\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"10\",\"FreeSpaceLong\":\"89\",\"ShortCapacity\":\"320\",\"LongCapacity\":\"168\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P02 Arena terrein\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9313054,52.31542] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P02 Arena terrein\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"2146\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"2200\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P10 Plaza ArenA\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9413056,52.307804] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P10 Plaza ArenA\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"790\",\"FreeSpaceLong\":\"21\",\"ShortCapacity\":\"877\",\"LongCapacity\":\"30\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P06 Byzantium\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.88,52.36189] }, \n" +
                " \"properties\": {\"Name\":\"CE-P06 Byzantium\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"102\",\"FreeSpaceLong\":\"94\",\"ShortCapacity\":\"362\",\"LongCapacity\":\"100\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P Willemspoort\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.884639,52.384888] }, \n" +
                " \"properties\": {\"Name\":\"CE-P Willemspoort\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"50\",\"FreeSpaceLong\":\"22\",\"ShortCapacity\":\"109\",\"LongCapacity\":\"34\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P01 Sloterdijk\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.838111,52.390083] }, \n" +
                " \"properties\": {\"Name\":\"CE-P01 Sloterdijk\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"2\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"196\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P13 Artis\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9136667,52.36811] }, \n" +
                " \"properties\": {\"Name\":\"CE-P13 Artis\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"357\",\"FreeSpaceLong\":\"0\",\"ShortCapacity\":\"562\",\"LongCapacity\":\"0\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P Oosterdok\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.909,52.376] }, \n" +
                " \"properties\": {\"Name\":\"CE-P Oosterdok\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"32\",\"FreeSpaceLong\":\"94\",\"ShortCapacity\":\"865\",\"LongCapacity\":\"100\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P06 Pathe/HMH\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9444165,52.3125] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P06 Pathe/HMH\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"154\",\"FreeSpaceLong\":\"2\",\"ShortCapacity\":\"363\",\"LongCapacity\":\"2\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P10 Stadhuis Muziektheater\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.900417,52.367584] }, \n" +
                " \"properties\": {\"Name\":\"CE-P10 Stadhuis Muziektheater\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"82\",\"FreeSpaceLong\":\"26\",\"ShortCapacity\":\"320\",\"LongCapacity\":\"29\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"CE-P05 Euro Parking\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.876639,52.36989] }, \n" +
                " \"properties\": {\"Name\":\"CE-P05 Euro Parking\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"warning\",\"FreeSpaceShort\":\"75\",\"FreeSpaceLong\":\"591\",\"ShortCapacity\":\"285\",\"LongCapacity\":\"725\"} \n" +
                "}\n" +
                ",\n" +
                "{ \"type\": \"Feature\", \"Id\": \"ZO-P03 Mikado\",\n" +
                " \"geometry\": {\"type\": \"Point\", \"coordinates\":[4.9398613,52.31086] }, \n" +
                " \"properties\": {\"Name\":\"ZO-P03 Mikado\",\"PubDate\":\"2015-05-15T15:08:21.000Z\",\"Type\":\"parkinglocation\",\"State\":\"ok\",\"FreeSpaceShort\":\"240\",\"FreeSpaceLong\":\"14\",\"ShortCapacity\":\"314\",\"LongCapacity\":\"20\"} \n" +
                "}\n" +
                "]}");

    }



}
