package com.inhatc.cinebook.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContentService {

    public void searchBook(String keyword) {

        RestTemplate restTemplate =
                new RestTemplate();

        String url =
                "https://dapi.kakao.com/v3/search/book?query="
                + keyword;

        HttpHeaders headers =
                new HttpHeaders();

        headers.set(
            "Authorization",
            "KakaoAK 3d4bd63464591703c4011d6f3cd332b0"
        );

        HttpEntity<String> entity =
                new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

        System.out.println(response.getBody());
    }
    
    public void searchMovie(String keyword) {

        RestTemplate restTemplate =
                new RestTemplate();

        String url =
                "https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp"
                + "?collection=kmdb_new2"
                + "&detail=Y"
                + "&title=" + keyword
                + "&ServiceKey=발급키";

        String result =
                restTemplate.getForObject(
                        url,
                        String.class
                );

        System.out.println(result);
    }
}
