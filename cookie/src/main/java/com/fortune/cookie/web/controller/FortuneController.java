package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.service.FortuneService;
import com.fortune.cookie.model.Fortune;
import com.fortune.cookie.swagger.DescriptionVariables;
import com.fortune.cookie.swagger.HTMLResponseMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Tag(name = DescriptionVariables.FORTUNE)
@Controller
@Validated
@RequestMapping("/api/v1")
public class FortuneController {

    @Autowired
    FortuneService fortuneService;

    @GetMapping("/fortunes")
    @Operation(summary = "Get all fortunes", description = "Get a list of administrator created fortunes",
    parameters = @Parameter(name = "Authorization", content = { @Content(mediaType = "application/json",
            examples = {@ExampleObject(value = "{\"Authorization\": \"Bearer value\"}")})}) )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Fortune.class))) }),
            @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<List<Fortune>> getAllFortunes() {
        List<Fortune> fortunes = fortuneService.getAllFortunes();
        return new ResponseEntity<>(fortunes, HttpStatus.OK);
    }

    @GetMapping("/fortunes/{id}")
    @Operation(summary = "Get a fortune", description = "Get a fortune by id",
            parameters = @Parameter(name = "Authorization", content = { @Content(mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"Authorization\": \"Bearer value\"}")})}) )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Fortune.class)) }),
            @ApiResponse(responseCode = "404", description = HTMLResponseMessages.HTTP_404, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Fortune> getFortuneById(@PathVariable Long id) {
        Fortune fortune = fortuneService.getFortuneById(id);
        if (fortune != null) {
            return new ResponseEntity<>(fortune, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/fortune/random")
    @Operation(summary = "Get a fortune", description = "Get a random fortune",
            parameters = @Parameter(name = "Authorization", content = { @Content(mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"Authorization\": \"Bearer value\"}")})}) )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Fortune.class)) }),
            @ApiResponse(responseCode = "404", description = HTMLResponseMessages.HTTP_404, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Fortune> getRandomFortune() {
        Fortune fortune = fortuneService.getRandomFortune();
        if (fortune != null) {
            return new ResponseEntity<>(fortune, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @DeleteMapping("/fortunes/{id}")
    @Operation(summary = "Delete fortune", description = "Delete a fortune by id",
            parameters = @Parameter(name = "Authorization", content = { @Content(mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"Authorization\": \"Bearer value\"}")})}) )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = HTMLResponseMessages.HTTP_204_WITHOUT_DATA,
                    content = @Content),
            @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Void> deleteFortune(@PathVariable Long id) {
        fortuneService.deleteFortune(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/fortunes")
    @Operation(summary = "Create a fortune", description = "Create a new fortune",
            parameters = @Parameter(name = "Authorization", content = { @Content(mediaType = "application/json",
            examples = {@ExampleObject(value = "{\"Authorization\": \"Bearer value\"}")})}) )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HTMLResponseMessages.HTTP_201,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Fortune.class)) }),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "404", description = HTMLResponseMessages.HTTP_404, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Fortune> createFortune(@RequestBody Map<String, Object> requestBody) {
        String sentence = (String) requestBody.get("sentence");
        List<String> descriptors = (List<String>) requestBody.get("descriptors");

        Fortune fortune = fortuneService.createFortune(sentence, descriptors != null ? new HashSet<>(descriptors) : new HashSet<>());
        return new ResponseEntity<>(fortune, HttpStatus.CREATED);
    }

    @PutMapping("/fortunes/{id}")
    @Operation(summary = "Update fortune", description = "Update a fortunes values",
            parameters = @Parameter(name = "Authorization", content = { @Content(mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"Authorization\": \"Bearer value\"}")})}),
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {@ExampleObject(value = "{\"sentence\": \"value\", " +
                    "\"descriptors\": [\"value\"]}")},
                    mediaType = MediaType.APPLICATION_JSON_VALUE)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Fortune.class)) }),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "404", description = HTMLResponseMessages.HTTP_404, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Fortune> updateFortune(@PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
        String sentence = (String) requestBody.getOrDefault("sentence", null);
        List<String> descriptors = (List<String>) requestBody.get("descriptors");

        Fortune fortune = fortuneService.updateFortune(id, sentence, descriptors != null ? new HashSet<>(descriptors) : new HashSet<>());
        return new ResponseEntity<>(fortune, HttpStatus.CREATED);
    }
}
