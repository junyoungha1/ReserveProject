package com.boot.reserveproject.controller;

import com.boot.reserveproject.domain.Camp;
import com.boot.reserveproject.repository.MemberLikesRepository;
import com.boot.reserveproject.service.CampService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SearchCategoryController {
    @Autowired
    private CampService campService;
    private final MemberLikesRepository memberLikesRepository;

    @GetMapping("/pc/search/category")
    public String showCategoryList(Model model) {
        return "pc/search/searchCategory";
    }

    @GetMapping("/mobile/search/category")
    public String showCategoryListMobile(Model model) {
        return "mobile/search/searchCategory";
    }

    public List<Camp> searchByAllList() {
        List<Camp> searchAllList = campService.findAllList();
        return searchAllList;
    }

    public List<Camp> searchByAnimal() {
        List<Camp> searchAllList = campService.findByAnimal();
        return searchAllList;
    }

    public List<Camp> searchByClturEventAt() {
        List<Camp> searchAllList = campService.findByClturEventAt();
        return searchAllList;
    }

    public List<Camp> searchByExprnProgrmAt() {
        List<Camp> searchAllList = campService.findByExprnProgrmAt();
        return searchAllList;
    }

    public List<Camp> searchByTrlerAcmpnyAt() {
        List<Camp> searchAllList = campService.findByTrlerAcmpnyAt();
        return searchAllList;
    }

    public List<Camp> searchByCaravAcmpnyAt() {
        List<Camp> searchAllList = campService.findByCaravAcmpnyAt();
        return searchAllList;
    }

    public List<Camp> searchByInduty(String keyword) {
        List<Camp> searchAllList = campService.findByInduty(keyword);
        return searchAllList;
    }

    public List<Camp> searchByBottom(String keyword) {
        List<Camp> searchAllList = campService.findByBottom(keyword);
        return searchAllList;
    }

    public List<Camp> searchByInduty(List<String> themas) {
        String[] themeNames = {"일출명소", "일몰명소", "걷기길", "봄꽃여행", "여름물놀이", "가을단풍명소", "겨울눈꽃명소", "수상레저", "항공레저", "스키", "낚시", "액티비티"};
        Set<Camp> searchAllSet = new LinkedHashSet<>();
        for (int i = 0; i < themas.size(); i++) {
            for (int j = 0; j < themeNames.length; j++) {
                if (themas.get(i).equals("thema" + String.format("%02d", j + 1))) {
                    themas.set(i, themeNames[j]);
                    searchAllSet.addAll(campService.findByThema(themas.get(i)));
                    break;
                }
            }
        }
        List<Camp> searchAllList = new ArrayList<>(searchAllSet);
        return checkList(searchAllList);
    }


    public List<Camp> searchByLctCls(List<String> lctCls) {
        String[] lctClNames = {"해변", "섬", "산", "숲", "계곡", "강", "호수", "도심"};
        for (int i = 0; i < lctCls.size(); i++) {
            for (int j = 0; j < lctClNames.length; j++) {
                if (lctCls.get(i).equals("lct" + String.format("%02d", j + 1))) {
                    lctCls.set(i, lctClNames[j]);
                    break;
                }
            }
        }
        List<Camp> searchAllList = campService.findByLctCls(lctCls);
        return searchAllList;
    }

    public List<Camp> checkList (List<Camp> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i+1; j < list.size(); j++) {
                if (list.get(i).getContentId().equals(list.get(j).getContentId())){
                    list.remove(j);
                    j--;
                }
            }
        }
        return list;
    }

    @GetMapping("/search/categoryCheck")
    public ResponseEntity<Object> campList(
            @RequestParam(value = "categories", required = false) String[] categories,
            @RequestParam(value = "pageNum", required = false) int pageNum, HttpSession session) {
        String log = (String) session.getAttribute("log");
        List<Long> memberLikesList = memberLikesRepository.selectMemberListByLoginId(log);
        List<Camp> mergedList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (categories.length == 0) {
            mergedList = searchByAllList();
        } else {
            List<String> themas = Arrays.stream(categories)
                    .filter(category -> category.contains("thema"))
                    .collect(Collectors.toList());
            if (!themas.isEmpty()) {
                mergedList.addAll(searchByInduty(themas));
            }

            List<String> lctCls = Arrays.stream(categories)
                    .filter(category -> category.contains("lct"))
                    .collect(Collectors.toList());
            if (!lctCls.isEmpty()) {
                mergedList.addAll(searchByLctCls(lctCls));
            }


            for (String category : categories) {
                switch (category) {
                    case "animalCmgCl":
                        mergedList.addAll(searchByAnimal());
                        continue;
                    case "clturEventAt":
                        mergedList.addAll(searchByClturEventAt());
                        continue;
                    case "exprnProgrmAt":
                        mergedList.addAll(searchByExprnProgrmAt());
                        continue;
                    case "trlerAcmpnyAt":
                        mergedList.addAll(searchByTrlerAcmpnyAt());
                        continue;
                    case "caravAcmpnyAt":
                        mergedList.addAll(searchByCaravAcmpnyAt());
                        continue;
                    case "nomal":
                        mergedList.addAll(searchByInduty("일반"));
                        continue;
                    case "carav":
                        mergedList.addAll(searchByInduty("카라반"));
                        continue;
                    case "glamp":
                        mergedList.addAll(searchByInduty("글램핑"));
                        continue;
                    case "car":
                        mergedList.addAll(searchByInduty("자동차"));
                        continue;
                    case "bottom01":
                        mergedList.addAll(searchByBottom("데크"));
                        continue;
                    case "bottom02":
                        mergedList.addAll(searchByBottom("파쇄석"));
                        continue;
                    case "bottom03":
                        mergedList.addAll(searchByBottom("잔디"));
                        continue;
                    case "bottom04":
                        mergedList.addAll(searchByBottom("자갈"));
                        continue;
                    case "bottom05":
                        mergedList.addAll(searchByBottom("맨흙"));
                        continue;
                    default:
                }
            }
        }
        mergedList = checkList(mergedList);
        Map<String, Object> resultMap = new LinkedHashMap<>();

        int min = pageNum * 10 - 10;
        int max = pageNum * 10;
        System.out.println("pageNum : " + pageNum + " / min : " + min + " / max : " + max);
        List<Camp> campList = new ArrayList<>();
        List<String> likeList = new ArrayList<>();
        for (int i = min; i < max && i < mergedList.size(); i++) {
            campList.add(mergedList.get(i));
            for(int j = 0;j<memberLikesList.size();j++){
                if(mergedList.get(i).getContentId()==memberLikesList.get(j)){
                    likeList.add("true");
                    break;
                }
            }
            if(campList.size()!=likeList.size()){
                likeList.add("false");
            }
            System.out.println("i = " + i + " / mergedList.get(i) : " + mergedList.get(i).getContentId());
        }
        resultMap.put("count", mergedList.size());
        resultMap.put("list", campList);
        resultMap.put("likeList", likeList);

        try {
            String jsonString = mapper.writeValueAsString(resultMap);
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
