package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.service.FortuneService;
import com.fortune.cookie.model.Fortune;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@Validated
@RequestMapping("/api/v1/fortunes")
public class FortuneController {

    @Autowired
    FortuneService fortuneService;

    @GetMapping
    public ResponseEntity<List<Fortune>> getAllFortunes() {
        List<Fortune> fortunes = fortuneService.getAllFortunes();
        return new ResponseEntity<>(fortunes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fortune> getFortuneById(@PathVariable Long id) {
        Fortune fortune = fortuneService.getFortuneById(id);
        if (fortune != null) {
            return new ResponseEntity<>(fortune, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFortune(@PathVariable Long id) {
        fortuneService.deleteFortune(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<Fortune> createFortune(@RequestBody Map<String, Object> requestBody) {
        String sentence = (String) requestBody.get("sentence");
        List<String> descriptors = (List<String>) requestBody.get("descriptors");

        Fortune fortune = fortuneService.createFortune(sentence, descriptors != null ? new HashSet<>(descriptors) : new HashSet<>());
        return new ResponseEntity<>(fortune, HttpStatus.CREATED);
    }
}
