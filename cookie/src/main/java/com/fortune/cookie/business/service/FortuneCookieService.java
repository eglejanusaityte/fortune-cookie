package com.fortune.cookie.business.service;

import com.fortune.cookie.business.dto.ModifiedFortuneCookieDTO;
import com.fortune.cookie.model.FortuneCookie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FortuneCookieService {
    FortuneCookie createFortuneCookie(String email);
    ModifiedFortuneCookieDTO createFortuneCookiePersonal(Long fortuneId, String email, List<String> words);
    ModifiedFortuneCookieDTO createFortuneCookieShort(String username);
    Page<ModifiedFortuneCookieDTO> getFortuneCookiesByUserId(Integer page, String username);
    Page<ModifiedFortuneCookieDTO> getFortuneCookies(Integer page);
    void deleteFortuneCookie(Long fortuneCookieId, String username);
    void likeFortuneCookie(Long fortuneCookieId, String username);
    void removeLikeFortuneCookie(Long fortuneCookieId, String username);
}
