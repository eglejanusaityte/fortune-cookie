package com.fortune.cookie.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FortuneCookieDTO {
    private Long id;
    private String username;
    private String fortuneCookieSentence;
    private LocalDate date;
    private Boolean personal;
    private Set<String> likes;
}