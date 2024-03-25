package com.fortune.cookie.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fortune {

    private Long id;

    @NonNull
    private String sentence;

    private Set<NeededWord> neededWords;

}
