package com.fortune.cookie.business.service;

import com.fortune.cookie.business.dto.ModifiedFortuneCookieDTO;
import com.fortune.cookie.model.FortuneCookie;
import com.fortune.cookie.model.User;

import java.util.List;

public interface FortuneCookieService {
    FortuneCookie createFortuneCookie(String email);
    FortuneCookie createFortuneCookiePersonal(Long fortuneId, String email, List<String> words);
    String createFortuneCookieShort(String email);
    List<ModifiedFortuneCookieDTO> getFortuneCookiesByUserId(String email);
    List<FortuneCookie> getFortuneCookies();
    void deleteFortuneCookie(Long fortuneCookieId, String email);
}
