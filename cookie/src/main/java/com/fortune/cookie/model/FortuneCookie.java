package com.fortune.cookie.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FortuneCookie {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date = LocalDate.now();

    @NonNull
    private Fortune fortune;

    private Set<Word> words;

    private User user;

    private List<Comment> comments;
}
