package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.mappers.WordMapper;
import com.fortune.cookie.business.repository.WordRepository;
import com.fortune.cookie.business.repository.model.WordDAO;
import com.fortune.cookie.business.service.WordService;
import com.fortune.cookie.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class WordServiceImpl implements WordService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordMapper wordMapper;

    @Override
    public Word getWordById(Long id) {
        Optional<WordDAO> optionalWordDAO = wordRepository.findById(id);
        return optionalWordDAO.map(wordMapper::wordDAOToWord).orElse(null);
    }

    @Override
    public Page<Word> getAllWords(Integer page) {
        Pageable wordPage = PageRequest.of(page, 10);
        return wordRepository.findByPersonalFalse(wordPage).map(wordMapper::wordDAOToWord);
    }

    @Override
    public Word createWord(Word word) {
        WordDAO wordDAO = wordMapper.wordToWordDAO(word);
        return wordMapper.wordDAOToWord(wordRepository.save(wordDAO));
    }

    @Override
    public void deleteWord(Long id) {
        wordRepository.deleteById(id);
    }

    @Override
    public Word editWord(Long id, Word word) {
        Optional<WordDAO> optionalExistingWordDAO = wordRepository.findById(id);
        if (optionalExistingWordDAO.isPresent()) {
            WordDAO existingWordDAO = optionalExistingWordDAO.get();
            WordDAO updatedWordDAO = wordMapper.wordToWordDAO(word);
            if (updatedWordDAO.getText() != null) {
                existingWordDAO.setText(updatedWordDAO.getText());
            }
            if (updatedWordDAO.getWordType() != null) {
                existingWordDAO.setWordType(updatedWordDAO.getWordType());
            }
            return wordMapper.wordDAOToWord(wordRepository.save(existingWordDAO));
        }
        return null;
    }

}