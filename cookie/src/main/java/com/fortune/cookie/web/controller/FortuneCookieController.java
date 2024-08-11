package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.dto.FortuneCookieDTO;
import com.fortune.cookie.business.service.FortuneCookieService;
import com.fortune.cookie.business.service.SecurityService;
import com.fortune.cookie.swagger.DescriptionVariables;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = DescriptionVariables.FORTUNE_COOKIE)
@RestController
@Validated
@RequestMapping("/api/v1")
public class FortuneCookieController {

    @Autowired
    private FortuneCookieService fortuneCookieService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/fortune-cookies")
    public ResponseEntity<FortuneCookieDTO> createFortuneCookie(HttpServletRequest request) {
        ResponseEntity<Map> response = securityService.security(request);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        FortuneCookieDTO fortuneCookie = fortuneCookieService.createFortuneCookieShort(username);
//        kafkaTemplate.send("cookie-requests", "Fortune cookie created", username);
        return ResponseEntity.ok(fortuneCookie);
    }

    @PostMapping("/fortune-cookie-personal")
    public ResponseEntity<FortuneCookieDTO> createFortuneCookiePersonal(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        Long fortuneId = Long.parseLong(requestBody.get("fortuneId").toString());
        List<String> words = (List<String>) requestBody.get("words");
        ResponseEntity<Map> response = securityService.security(request);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        FortuneCookieDTO fortuneCookie = fortuneCookieService.createFortuneCookiePersonal(fortuneId, username, words);
        return ResponseEntity.ok(fortuneCookie);
    }

    @GetMapping("/fortune-cookies-personal")
    public ResponseEntity<Page<FortuneCookieDTO>> getFortuneCookiesByUser(@RequestParam(value = "page", defaultValue = "0") Integer page, HttpServletRequest request) {
        ResponseEntity<Map> response = securityService.security(request);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        Page<FortuneCookieDTO> fortuneCookies = fortuneCookieService.getFortuneCookiesByUserId(page, username);
        return ResponseEntity.ok(fortuneCookies);
    }

    @GetMapping("/fortune-cookies")
    public ResponseEntity<Page<FortuneCookieDTO>> getFortuneCookies(@RequestParam(value = "page", defaultValue = "0") Integer page, HttpServletRequest request) {
        Page<FortuneCookieDTO> fortuneCookies = fortuneCookieService.getFortuneCookies(page);
        return ResponseEntity.ok(fortuneCookies);
    }

    @DeleteMapping("/fortune-cookie/{fortuneCookieId}")
    public ResponseEntity<Void> deleteFortuneCookie(@PathVariable Long fortuneCookieId, HttpServletRequest request) {
        ResponseEntity<Map> response = securityService.security(request);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        fortuneCookieService.deleteFortuneCookie(fortuneCookieId, username);
        return ResponseEntity.noContent().build();
    }

    //add like
    @PutMapping("/fortune-cookies/{fortuneCookieId}/like")
    public ResponseEntity<Void> likeFortuneCookie(@PathVariable Long fortuneCookieId, HttpServletRequest request) {
        ResponseEntity<Map> response = securityService.security(request);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        fortuneCookieService.likeFortuneCookie(fortuneCookieId, username);
        return ResponseEntity.noContent().build();
    }

    // remove like
    @PutMapping("/fortune-cookies/{fortuneCookieId}/remove")
    public ResponseEntity<Void> removeLikeFortuneCookie(@PathVariable Long fortuneCookieId, HttpServletRequest request) {
        ResponseEntity<Map> response = securityService.security(request);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        fortuneCookieService.removeLikeFortuneCookie(fortuneCookieId, username);
        return ResponseEntity.noContent().build();
    }
}
