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
public class NeededWord {

    @Schema(description = "The unique id of the user",
            example = "1")
    private Long id;

    @NonNull
    @Schema(description = "Enumeration of word types", example = "NOUN")
    private WordType wordType;

    @NonNull
    @Schema(description = "Description of the word used for mad libs", example = "A weapon of sorts")
    private String descriptor;

}
