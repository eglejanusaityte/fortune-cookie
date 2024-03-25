package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.service.WordService;
import com.fortune.cookie.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Controller
@Validated
@RequestMapping("/api/v1/words")
public class WordController {

    @Autowired
    WordService wordService;

    @GetMapping("/{id}")
    public ResponseEntity<Word> getWordById(@PathVariable Long id) {
        Word word = wordService.getWordById(id);
        if (word != null) {
            return ResponseEntity.ok(word);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Word>> getAllWords() {
        List<Word> words = wordService.getAllWords();
        return ResponseEntity.ok(words);
    }

    @PostMapping
    public ResponseEntity<Word> createWord(@RequestBody Word word) {
        Word createdWord = wordService.createWord(word);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWord);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Word> editWord(@PathVariable Long id, @RequestBody Word word) {
        Word editedWord = wordService.editWord(id, word);
        if (editedWord != null) {
            return ResponseEntity.ok(editedWord);
        }
        return ResponseEntity.notFound().build();
    }
}
