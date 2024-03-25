package com.fortune.cookie.model;

import com.fortune.cookie.business.enums.WordType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Word {

    private Long id;

    @NonNull
    private String word;

    @NonNull
    private WordType wordType;

}
