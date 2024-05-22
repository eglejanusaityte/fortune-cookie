package com.fortune.cookie.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FortuneCookie {

    @Schema(description = "The unique id of the user",
            example = "1")
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "The date of fortune cookie",
            example = "2024-04-10")
    private LocalDate date = LocalDate.now();

    @NonNull
    @Schema(description = "The random fortune used in fortune cookie")
    private Fortune fortune;

    @Schema(description = "List of words used to fill out the fortune")
    private Set<Word> words;

    @Schema(description = "User to which the cookie belongs to")
    private User user;

}
