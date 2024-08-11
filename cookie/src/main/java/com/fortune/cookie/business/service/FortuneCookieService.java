package com.fortune.cookie.business.service;

import com.fortune.cookie.business.dto.FortuneCookieDTO;
import com.fortune.cookie.model.FortuneCookie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FortuneCookieService {
    FortuneCookie createFortuneCookie(String email);
    FortuneCookieDTO createFortuneCookiePersonal(Long fortuneId, String email, List<String> words);
    FortuneCookieDTO createFortuneCookieShort(String username);
    Page<FortuneCookieDTO> getFortuneCookiesByUserId(Integer page, String username);
    Page<FortuneCookieDTO> getFortuneCookies(Integer page);
    void deleteFortuneCookie(Long fortuneCookieId, String username);
    void likeFortuneCookie(Long fortuneCookieId, String username);
    void removeLikeFortuneCookie(Long fortuneCookieId, String username);
}
