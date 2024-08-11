package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.enums.WordType;
import com.fortune.cookie.business.mappers.WordMapper;
import com.fortune.cookie.business.repository.WordRepository;
import com.fortune.cookie.business.repository.model.WordDAO;
import com.fortune.cookie.model.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WordServiceImplTest {

    @Mock
    private WordRepository wordRepository;

    @Mock
    private WordMapper wordMapper;

    @InjectMocks
    private WordServiceImpl wordService;

    private WordDAO wordDAO;
    private Word word;

    @BeforeEach
    void setUp() {
        wordDAO = new WordDAO();
        wordDAO.setId(1L);
        wordDAO.setText("happy");
        wordDAO.setWordType(WordType.ADJECTIVE);

        word = new Word();
        word.setId(1L);
        word.setText("happy");
        word.setWordType(WordType.ADJECTIVE);
    }

    @Test
    void testGetWordById_WordFound() {
        when(wordRepository.findById(1L)).thenReturn(Optional.of(wordDAO));
        when(wordMapper.wordDAOToWord(wordDAO)).thenReturn(word);

        Word result = wordService.getWordById(1L);

        assertNotNull(result);
        assertEquals("happy", result.getText());
        verify(wordRepository).findById(1L);
        verify(wordMapper).wordDAOToWord(wordDAO);
    }

    @Test
    void testGetWordById_WordNotFound() {
        when(wordRepository.findById(1L)).thenReturn(Optional.empty());

        Word result = wordService.getWordById(1L);

        assertNull(result);
        verify(wordRepository).findById(1L);
        verify(wordMapper, never()).wordDAOToWord(any(WordDAO.class));
    }

    @Test
    void testGetAllWords() {
        List<WordDAO> wordDAOList = Arrays.asList(wordDAO);
        Page<WordDAO> wordDAOPage = new PageImpl<>(wordDAOList);
        when(wordRepository.findByPersonalFalse(any(Pageable.class))).thenReturn(wordDAOPage);
        when(wordMapper.wordDAOToWord(wordDAO)).thenReturn(word);

        Page<Word> result = wordService.getAllWords(0);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("happy", result.getContent().get(0).getText());
        verify(wordRepository).findByPersonalFalse(any(Pageable.class));
        verify(wordMapper).wordDAOToWord(wordDAO);
    }

    @Test
    void testCreateWord() {
        when(wordMapper.wordToWordDAO(word)).thenReturn(wordDAO);
        when(wordRepository.save(wordDAO)).thenReturn(wordDAO);
        when(wordMapper.wordDAOToWord(wordDAO)).thenReturn(word);

        Word result = wordService.createWord(word);

        assertNotNull(result);
        assertEquals("happy", result.getText());
        verify(wordMapper).wordToWordDAO(word);
        verify(wordRepository).save(wordDAO);
        verify(wordMapper).wordDAOToWord(wordDAO);
    }

    @Test
    void testDeleteWord() {
        doNothing().when(wordRepository).deleteById(1L);

        wordService.deleteWord(1L);

        verify(wordRepository).deleteById(1L);
    }

    @Test
    void testEditWord_WordFound() {
        Word updatedWord = new Word();
        updatedWord.setText("joyful");
        updatedWord.setWordType(WordType.ADJECTIVE);

        WordDAO updatedWordDAO = new WordDAO();
        updatedWordDAO.setText("joyful");
        updatedWordDAO.setWordType(WordType.ADJECTIVE);

        when(wordRepository.findById(1L)).thenReturn(Optional.of(wordDAO));
        when(wordMapper.wordToWordDAO(updatedWord)).thenReturn(updatedWordDAO);
        when(wordRepository.save(any(WordDAO.class))).thenReturn(wordDAO);
        word.setText("joyful");
        when(wordMapper.wordDAOToWord(wordDAO)).thenReturn(word);

        Word result = wordService.editWord(1L, updatedWord);

        assertNotNull(result);
        assertEquals("joyful", result.getText());
        verify(wordRepository).findById(1L);
        verify(wordMapper).wordToWordDAO(updatedWord);
        verify(wordRepository).save(any(WordDAO.class));
        verify(wordMapper).wordDAOToWord(wordDAO);
    }

    @Test
    void testEditWord_WordNotFound() {
        when(wordRepository.findById(1L)).thenReturn(Optional.empty());

        Word result = wordService.editWord(1L, word);

        assertNull(result);
        verify(wordRepository).findById(1L);
        verify(wordMapper, never()).wordToWordDAO(any(Word.class));
        verify(wordRepository, never()).save(any(WordDAO.class));
    }

}
