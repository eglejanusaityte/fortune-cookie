package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.service.WordService;
import com.fortune.cookie.model.Word;
import com.fortune.cookie.swagger.DescriptionVariables;
import com.fortune.cookie.swagger.HTMLResponseMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = DescriptionVariables.WORD)
@RestController
@Validated
@RequestMapping("/api/v1/words")
public class WordController {

    @Autowired
    WordService wordService;

    @GetMapping("/{id}")
    @Operation(summary = "Get word", description = "Get a word by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Word.class)) }),
            @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Word> getWordById(@PathVariable Long id) {
        Word word = wordService.getWordById(id);
        if (word != null) {
            return ResponseEntity.ok(word);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all words", description = "Get all existing words")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Word.class))) }),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Page<Word>> getAllWords(@RequestParam(value = "page", defaultValue = "0") Integer page) {
        Page<Word> words = wordService.getAllWords(page);
        return ResponseEntity.ok(words);
    }

    @PostMapping
    @Operation(summary = "Create word", description = "Create a word")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Word.class)) }),
            @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Word> createWord(@RequestBody Word word) {
        Word createdWord = wordService.createWord(word);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWord);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete word", description = "Delete a word by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = HTMLResponseMessages.HTTP_204_WITHOUT_DATA,
                    content = @Content),
            @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit word", description = "Edit word values by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Word.class)) }),
            @ApiResponse(responseCode = "400", description = HTMLResponseMessages.HTTP_400, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Word> editWord(@PathVariable Long id, @RequestBody Word word) {
        Word editedWord = wordService.editWord(id, word);
        if (editedWord != null) {
            return ResponseEntity.ok(editedWord);
        }
        return ResponseEntity.notFound().build();
    }
}
