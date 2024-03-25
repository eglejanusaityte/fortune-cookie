package com.fortune.cookie.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    private Long id;

    @NonNull
    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date = LocalDate.now();

    @NonNull
    private User user;

    @NonNull
    private Long fortuneCookieId;
}
