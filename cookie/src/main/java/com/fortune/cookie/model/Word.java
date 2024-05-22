package com.fortune.cookie.model;

import com.fortune.cookie.business.enums.WordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    @Schema(description = "The unique id of the user",
            example = "1")
    private Long id;

    @Schema(description = "The word string itself", example = "Word")
    @NonNull
    private String word;

    @Schema(description = "Enumeration of word types", example = "NOUN")
    @NonNull
    private WordType wordType;

    @Schema(description = "If word was created by user playing mad libs", example = "true")
    @NonNull
    private Boolean personal;

}
