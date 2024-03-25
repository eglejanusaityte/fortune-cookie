package com.fortune.cookie.business.service;

import com.fortune.cookie.model.NeededWord;

import java.util.List;

public interface NeededWordService {
    NeededWord getNeededWordById(Long id);

    List<NeededWord> getAllNeededWords();

    NeededWord editNeededWord(NeededWord neededWord);

    void deleteNeededWord(Long id);
}