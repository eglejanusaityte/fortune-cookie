package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.service.FortuneService;
import com.fortune.cookie.model.Fortune;
import com.fortune.cookie.swagger.DescriptionVariables;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Tag(name = DescriptionVariables.FORTUNE)
@Controller
@Validated
@RequestMapping("/api/v1")
public class FortuneController {

    @Autowired
    FortuneService fortuneService;

    @GetMapping("/fortunes")
    public ResponseEntity<Page<Fortune>> getAllFortunes(@RequestParam(value = "page", defaultValue = "0") Integer page) {
        Page<Fortune> fortunes = fortuneService.getAllFortunes(page);
        return new ResponseEntity<>(fortunes, HttpStatus.OK);
    }

    @GetMapping("/fortunes/{id}")
    public ResponseEntity<Fortune> getFortuneById(@PathVariable Long id) {
        Fortune fortune = fortuneService.getFortuneById(id);
        if (fortune != null) {
            return new ResponseEntity<>(fortune, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/fortune/random")
    public ResponseEntity<Fortune> getRandomFortune() {
        Fortune fortune = fortuneService.getRandomFortune();
        if (fortune != null) {
            return new ResponseEntity<>(fortune, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/fortunes/{id}")
    public ResponseEntity<Void> deleteFortune(@PathVariable Long id) {
        fortuneService.deleteFortune(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/fortunes")
    public ResponseEntity<Fortune> createFortune(@RequestBody Map<String, Object> requestBody) {
        String sentence = (String) requestBody.get("sentence");
        List<Map<String, String>> neededWords = (List<Map<String, String>>) requestBody.get("neededWords");

        Fortune fortune = fortuneService.createFortune(sentence, neededWords);
        return new ResponseEntity<>(fortune, HttpStatus.CREATED);
    }

    @PutMapping("/fortunes/{id}")
    public ResponseEntity<Fortune> updateFortune(@PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
        String sentence = (String) requestBody.getOrDefault("sentence", null);
        List<Map<String, String>> neededWords = (List<Map<String, String>>) requestBody.get("neededWords");

        Fortune fortune = fortuneService.updateFortune(id, sentence, neededWords);
        return new ResponseEntity<>(fortune, HttpStatus.CREATED);
    }
}
