package com.fortune.cookie.model;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Schema(description = "Model of Fortune")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fortune {

    @Schema(description = "The unique id of the user",
            example = "1")
    private Long id;

    @Schema(description = "The fortune sentence with hashtags marking where words will be filled in",
            example = "This is an example #NOUN")
    private String sentence;

    @Schema(description = "Needed words list",
            example = "[{\"WordType\": \"NOUN\",\"descriptors\": [\"value\"]}]")
    private Set<NeededWord> neededWords;

}
