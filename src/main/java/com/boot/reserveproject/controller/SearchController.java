package com.boot.reserveproject.controller;

import com.boot.reserveproject.domain.Camp;
import com.boot.reserveproject.repository.MemberLikesRepository;
import com.boot.reserveproject.service.CampService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final CampService campService;
    private final MemberLikesRepository memberLikesRepository;
    @GetMapping("/pc/search/map")
    public String showMapIntroPc() {
        return "pc/search/searchMap";
    }
    @GetMapping("/mobile/search/map")
    public String showMapIntroMobile() {
        return "mobile/search/searchMap";
    }
    @GetMapping("/search/mapSearch")
    public ResponseEntity<Object> showMap(@RequestParam(value = "boundsObj", required = false) String boundsObjStr, @RequestParam(value="keyword", required=false) String keyword, @RequestParam(value = "type", required = false) String type) {
        JSONObject boundsObjJson = new JSONObject(boundsObjStr);
        double southWestLat = boundsObjJson.getJSONObject("southWest").getDouble("y");
        double southWestLng = boundsObjJson.getJSONObject("southWest").getDouble("x");
        double northEastLat = boundsObjJson.getJSONObject("northEast").getDouble("y");
        double northEastLng = boundsObjJson.getJSONObject("northEast").getDouble("x");


        List<Camp> campList = new ArrayList<>();
        try {
            campList=searchWithBounds(southWestLat,southWestLng,northEastLat,northEastLng);

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(campList);
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/pc/search/location")
    public String showAddressListPc(Model model){
        campString(model);
        return "pc/search/searchLocation";
    }
    @GetMapping("/mobile/search/location")
    public String showAddressListMobile(Model model){
        campString(model);
        return "mobile/search/searchLocation";
    }

    @GetMapping("search/addressSearchTest")
    public ResponseEntity<Object> showListByAddressTest(
            Model model,
            @RequestParam(value="pageNum",required=false) int pageNum,
            @RequestParam(value = "sido", required = false) String sido,
            @RequestParam(value="sigoon", required=false) String sigoon,
            @RequestParam(value="pageRequest", required = false) int pageRequest) {
        if(sido.equals("전체")){
            sido="";
        }
        if(sigoon.equals("전체")){
            sigoon="";
        }
        System.out.println("sido: "+sido+" sigoon:  "+sigoon);
        Long length=countListByLocation(sido,sigoon);
        List<Camp> campList = searchByLocationTest(model,sido,sigoon,pageNum,pageRequest);
        List<Long> memberLikesList = memberLikesRepository.selectMemberListByLoginId(log);
        List<String> likeList = new ArrayList<>();
        System.out.println("지역검색 캠프리스트 길이: "+campList.size());
        Map<String, Object> response = new HashMap<>();
        response.put("campList", campList);
        response.put("length", length);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(response);
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @GetMapping("search/addressSearchTestTest")
    public ResponseEntity<Object> showListByAddressTestTest(Model model, @RequestParam(value="pageNum",required=false) int pageNum, @RequestParam(value = "sido", required = false) String sido, @RequestParam(value="sigoon", required=false) String sigoon,@RequestParam(value="pageRequest", required = false) int pageRequest) {
        if(sido.equals("전체")){
            sido="";
        }
        if(sigoon.equals("전체")){
            sigoon="";
        }
        System.out.println("sido: "+sido+" sigoon:  "+sigoon);

        List<Camp> campList = searchByLocationTest(model,sido,sigoon,pageNum,pageRequest);
        System.out.println("지역검색 캠프리스트 길이: "+campList.size());
        Map<String, Object> response = new HashMap<>();
        response.put("campList", campList);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(response);
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


public List<Camp> searchWithBounds(Double southWestLat, Double southWestLng, Double northEastLat, Double northEastLng){
    List<Camp> campList= campService.getCampListByBounds(southWestLat,southWestLng,northEastLat,northEastLng);

    return campList;
}


    public List<Camp> searchByLocationTest(Model model,String sido,String sigoon,int pageNum,int pageRequest){
        Pageable pageable = PageRequest.of(pageNum - 1, pageRequest); // 페이지 번호와 페이지 크기를 설정합니다.

        List<Camp> campList = campService.findByDoNmContainingAndSigunguNmContainingOrderById(sido, sigoon, pageable);

        model.addAttribute("campList", campList);
        return campList;
    }
    private void campString(Model model) {
        List<Camp> campList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Camp camp = campService.selectOneById((long) i);
            System.out.println("i = " + i);
            if (camp != null) {
                campList.add(camp);
            }
        }
        model.addAttribute("campList", campList);

        List<List<String>> sbrsClList = new ArrayList<>();
        List<List<String>> themaList = new ArrayList<>();
        for (int i = 0; i < campList.size(); i++) {
            String tempSbrsCl = campList.get(i).getSbrsCl();
            String[] sbrsCl = tempSbrsCl.split(",");
            sbrsClList.add(Arrays.asList(sbrsCl));

            String[] thema = {"즐거운캠핑"};
            if (!campList.get(i).getThemaEnvrnCl().equals("")) {
                thema = campList.get(i).getThemaEnvrnCl().split(",");
            }
            themaList.add(Arrays.asList(thema));

            System.out.println(Arrays.toString(thema));
            System.out.println(Arrays.toString(sbrsCl));
        }
        model.addAttribute("sbrsClList", sbrsClList);
        model.addAttribute("themaList", themaList);
    }
    public Long countListByLocation(String sido,String sigoon){
        Long length= campService.countListByLocation(sido,sigoon);
        return length;
    }
}
