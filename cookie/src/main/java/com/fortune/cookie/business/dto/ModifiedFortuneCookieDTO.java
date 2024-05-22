package com.fortune.cookie.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModifiedFortuneCookieDTO {
    private String username;
    private String fortuneCookieSentence;
    private LocalDate date;

}