package com.fortune.cookie.business.service;

import com.fortune.cookie.model.Word;

import java.util.List;

public interface WordService {
    Word getWordById(Long id);

    List<Word> getAllWords();

    Word createWord(Word word);

    void deleteWord(Long id);

    Word editWord(Long id, Word word);
}