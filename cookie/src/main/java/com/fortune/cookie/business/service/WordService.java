package com.fortune.cookie.business.service;

import com.fortune.cookie.model.Word;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WordService {
    Word getWordById(Long id);

    Page<Word> getAllWords(Integer page);

    Word createWord(Word word);

    void deleteWord(Long id);

    Word editWord(Long id, Word word);
}