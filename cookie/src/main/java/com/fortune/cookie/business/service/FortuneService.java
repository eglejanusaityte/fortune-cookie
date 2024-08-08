package com.fortune.cookie.business.service;

import com.fortune.cookie.model.Fortune;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FortuneService {

    Fortune getFortuneById(Long id);

    Fortune getRandomFortune();

    Fortune getFortune();

    Page<Fortune> getAllFortunes(Integer page);

    void deleteFortune(Long id);

    Fortune createFortune(String sentence, List<Map<String, String>> neededWords);

    Fortune updateFortune(Long fortuneId, String sentence, List<Map<String, String>> neededWords);

}