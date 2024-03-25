package com.fortune.cookie.business.service;

import com.fortune.cookie.model.Fortune;

import java.util.List;
import java.util.Set;

public interface FortuneService {

    Fortune getFortuneById(Long id);

    List<Fortune> getAllFortunes();

    void deleteFortune(Long id);

    Fortune createFortune(String sentence, Set<String> descriptors);
}