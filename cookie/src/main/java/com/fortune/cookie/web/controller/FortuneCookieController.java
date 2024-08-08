package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.dto.ModifiedFortuneCookieDTO;
import com.fortune.cookie.business.service.FortuneCookieService;
import com.fortune.cookie.model.Fortune;
import com.fortune.cookie.model.FortuneCookie;
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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
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
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = DescriptionVariables.FORTUNE_COOKIE)
@RestController
@Validated
@RequestMapping("/api/v1")
public class FortuneCookieController {

    @Autowired
    private FortuneCookieService fortuneCookieService;

    @Autowired
    private RestTemplate restTemplate;

    private final String USERNAME_URL = "http://localhost:8081/api/v1/jwt/extract-username";


    private ResponseEntity<Map> security(HttpServletRequest request, String url){
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = authorizationHeader.substring(7);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                jwtToken,
                Map.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to get results from security microservice");
        }
        return response;
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @PostMapping("/fortune-cookies")
    @Operation(summary = "Create fortune cookie", description = "Creates a new fortune cookie for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = @Content(examples = {@ExampleObject(value = "{\"sentence\": \"value\"}")},
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<ModifiedFortuneCookieDTO> createFortuneCookie(HttpServletRequest request) {
        ResponseEntity<Map> response = security(request, USERNAME_URL);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        ModifiedFortuneCookieDTO fortuneCookie = fortuneCookieService.createFortuneCookieShort(username);

//        kafkaTemplate.send("cookie-requests", "Fortune cookie created", username);
        return ResponseEntity.ok(fortuneCookie);
    }

    @PostMapping("/fortune-cookie-personal")
    @Operation(summary = "Create fortune cookie mad lib",
            description = "Creates a new fortune cookie for user based on user inputs of needed words", requestBody =
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = {@ExampleObject(value = "{\"fortuneId\": \"value\", \"words\": [\"value\"]}")},
                    mediaType = MediaType.APPLICATION_JSON_VALUE)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FortuneCookie.class)) }
            ),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<ModifiedFortuneCookieDTO> createFortuneCookiePersonal(@RequestBody Map<String, Object> requestBody,
                                                                     HttpServletRequest request) {

        Long fortuneId = Long.parseLong(requestBody.get("fortuneId").toString());
        List<String> words = (List<String>) requestBody.get("words");
        ResponseEntity<Map> response = security(request, USERNAME_URL);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        ModifiedFortuneCookieDTO fortuneCookie = fortuneCookieService.createFortuneCookiePersonal(fortuneId, username, words);
        return ResponseEntity.ok(fortuneCookie);
    }

    @Operation(summary = "Get user's fortune cookie", description = "Get all of user's fortune cookies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FortuneCookie.class))
                    )
            ),
            @ApiResponse(responseCode = "204", description = HTMLResponseMessages.HTTP_204, content = @Content),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    @GetMapping("/fortune-cookies-personal")
    public ResponseEntity<Page<ModifiedFortuneCookieDTO>> getFortuneCookiesByUser(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                  HttpServletRequest request) {
        ResponseEntity<Map> response = security(request, USERNAME_URL);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        Page<ModifiedFortuneCookieDTO> fortuneCookies = fortuneCookieService.getFortuneCookiesByUserId(page, username);
        if (fortuneCookies.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(fortuneCookies);
        }
    }

    @GetMapping("/fortune-cookies")
    @Operation(summary = "Get all fortune cookies", description = "Get all fortune cookies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FortuneCookie.class))
                    )
            ),
            @ApiResponse(responseCode = "204", description = HTMLResponseMessages.HTTP_204, content = @Content),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Page<ModifiedFortuneCookieDTO>> getFortuneCookies(
            @RequestParam(value = "page", defaultValue = "0") Integer page, HttpServletRequest request) {
        Page<ModifiedFortuneCookieDTO> fortuneCookies = fortuneCookieService.getFortuneCookies(page);
        if (fortuneCookies.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(fortuneCookies);
        }
    }

    @DeleteMapping("/fortune-cookie/{fortuneCookieId}")
    @Operation(summary = "Delete fortune cookie", description = "Delete a fortune cookie",
            parameters = @Parameter(name = "Authorization", content = { @Content(mediaType = "application/json",
                    examples = {@ExampleObject(value = "{\"Authorization\": \"Bearer value\"}")})}),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {@ExampleObject(value = "{\"fortuneCookieId\": \"value\"}")},
                            mediaType = MediaType.APPLICATION_JSON_VALUE)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = HTMLResponseMessages.HTTP_204_WITHOUT_DATA,
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Fortune.class)) }),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Void> deleteFortuneCookie(
            @PathVariable Long fortuneCookieId, HttpServletRequest request) {
        ResponseEntity<Map> response = security(request, USERNAME_URL);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        fortuneCookieService.deleteFortuneCookie(fortuneCookieId, username);
        return ResponseEntity.noContent().build();
    }

    //add like
    @PutMapping("/fortune-cookies/{fortuneCookieId}/like")
    @Operation(summary = "Like a fortune cookie", description = "Adds user to fortune cookie liked list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = @Content(examples = {@ExampleObject(value = "{\"sentence\": \"value\"}")},
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Void> likeFortuneCookie(
            @PathVariable Long fortuneCookieId, HttpServletRequest request) {
        ResponseEntity<Map> response = security(request, USERNAME_URL);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        fortuneCookieService.likeFortuneCookie(fortuneCookieId, username);
        return ResponseEntity.noContent().build();
    }

    // remove like
    @PutMapping("/fortune-cookies/{fortuneCookieId}/remove")
    @Operation(summary = "Remove like from a fortune cookie", description = "Removes user from fortune cookie liked list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HTMLResponseMessages.HTTP_200,
                    content = @Content(examples = {@ExampleObject(value = "{\"sentence\": \"value\"}")},
                            mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(responseCode = "403", description = HTMLResponseMessages.HTTP_403, content = @Content),
            @ApiResponse(responseCode = "500", description = HTMLResponseMessages.HTTP_500, content = @Content),
    })
    public ResponseEntity<Void> removeLikeFortuneCookie(
            @PathVariable Long fortuneCookieId, HttpServletRequest request) {
        ResponseEntity<Map> response = security(request, USERNAME_URL);
        String username = (String) Objects.requireNonNull(response.getBody()).get("username");
        fortuneCookieService.removeLikeFortuneCookie(fortuneCookieId, username);
        return ResponseEntity.noContent().build();
    }
}
