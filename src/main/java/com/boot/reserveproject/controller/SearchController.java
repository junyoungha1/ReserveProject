package com.boot.reserveproject.controller;

import com.boot.reserveproject.domain.Camp;
import com.boot.reserveproject.repository.CampRepository;
import com.boot.reserveproject.service.CampService;
import com.boot.reserveproject.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final CampService campService;
    @GetMapping("/search/map")
    public String showMapIntro() {
        return "pc/search/searchMap";
    }

    @GetMapping("/search/mapSearch")
    public ResponseEntity<Object> showMap(@RequestParam(value = "boundsObj", required = false) String boundsObjStr, @RequestParam(value="keyword", required=false) String keyword, @RequestParam(value = "type", required = false) String type) {
        JSONObject boundsObjJson = new JSONObject(boundsObjStr);
        double southWestLat = boundsObjJson.getJSONObject("southWest").getDouble("y");
        double southWestLng = boundsObjJson.getJSONObject("southWest").getDouble("x");
        double northEastLat = boundsObjJson.getJSONObject("northEast").getDouble("y");
        double northEastLng = boundsObjJson.getJSONObject("northEast").getDouble("x");
        double[]arr={southWestLat,southWestLng,northEastLng,northEastLat};

        if (keyword == null|| type == null) {

            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/search/searchMap").build();
        }

        List<Camp> campList = new ArrayList<>();
        try {
            if (type.equals("name")) {
                campList = searchByName(southWestLat,southWestLng,northEastLat,northEastLng,keyword);




            } else if (type.equals("theme")) {
                campList = searchByTheme(southWestLat,southWestLng,northEastLat,northEastLng,keyword);
            } else if (type.equals("location")) {
                campList = searchByAddress(southWestLat,southWestLng,northEastLat,northEastLng,keyword);
            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(campList);
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public List<Camp> searchByName(Double southWestLat,Double southWestLng,Double northEastLat,Double northEastLng,String keyword) {
        List<Camp> campList= campService.getCampListByName(southWestLat,southWestLng,northEastLat,northEastLng,keyword);

        return campList;
    }

    public List<Camp> searchByTheme(Double southWestLat,Double southWestLng,Double northEastLat,Double northEastLng,String keyword) {
        List<Camp> campList=campService.getCampListByTheme(southWestLat,southWestLng,northEastLat,northEastLng,keyword);

        return campList;
    }

    public List<Camp> searchByAddress(Double southWestLat,Double southWestLng,Double northEastLat,Double northEastLng,String keyword) {
        List<Camp>campList=campService.getCampListByAddress(southWestLat,southWestLng,northEastLat,northEastLng,keyword);

        return campList;

    }
}
