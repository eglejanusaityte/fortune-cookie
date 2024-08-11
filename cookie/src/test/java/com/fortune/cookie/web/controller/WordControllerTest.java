package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.enums.WordType;
import com.fortune.cookie.business.service.WordService;
import com.fortune.cookie.config.JwtAuthenticationFilter;
import com.fortune.cookie.model.Word;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
public class WordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordService wordService;

    private Page<Word> wordPage;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Word testWord;

    @BeforeEach
    public void setup() throws ServletException, IOException {
        testWord = new Word();
        testWord.setId(1L);
        testWord.setWordType(WordType.ADJECTIVE);
        testWord.setText("Nice");

        wordPage = new PageImpl<>(Collections.singletonList(testWord), PageRequest.of(0, 10), 1);

        when(wordService.getAllWords(0)).thenReturn(wordPage);
        when(wordService.getWordById(1L)).thenReturn(testWord);

        // mock jwt filtering, because not relevant in current ms
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class), any(FilterChain.class));
    }
    
    @Test
    public void getWordById_whenWordExists_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/words/1")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Nice"))
                .andExpect(jsonPath("$.wordType").value("ADJECTIVE"));
    }

    @Test
    public void getWordById_whenWordDoesNotExist_shouldReturn404() throws Exception {
        when(wordService.getWordById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/words/1")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllWords_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/words")
                        .param("page", "0")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].text").value("Nice"))
                .andExpect(jsonPath("$.content[0].wordType").value("ADJECTIVE"));
    }

    @Test
    public void createWord_shouldReturn201() throws Exception {
        Word newWord = new Word();
        newWord.setId(2L);
        newWord.setWordType(WordType.NOUN);
        newWord.setText("Example");

        when(wordService.createWord(any(Word.class))).thenReturn(newWord);

        mockMvc.perform(post("/api/v1/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Example\", \"wordType\": \"NOUN\"}")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.text").value("Example"))
                .andExpect(jsonPath("$.wordType").value("NOUN"));
    }

    @Test
    public void deleteWord_shouldReturn204() throws Exception {
        doNothing().when(wordService).deleteWord(1L);

        mockMvc.perform(delete("/api/v1/words/1")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void editWord_whenWordExists_shouldReturn200() throws Exception {
        Word updatedWord = new Word();
        updatedWord.setId(1L);
        updatedWord.setWordType(WordType.ADJECTIVE);
        updatedWord.setText("Updated");

        when(wordService.editWord(eq(1L), any(Word.class))).thenAnswer(invocation -> {
            Word word = invocation.getArgument(1);
            word.setId(1L);
            return word;
        });

        mockMvc.perform(put("/api/v1/words/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Updated\", \"wordType\": \"ADJECTIVE\"}")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Updated"))
                .andExpect(jsonPath("$.wordType").value("ADJECTIVE"));
    }

    @Test
    public void editWord_whenWordDoesNotExist_shouldReturn404() throws Exception {
        Word updatedWord = new Word();
        updatedWord.setId(1L);
        updatedWord.setWordType(WordType.ADJECTIVE);
        updatedWord.setText("Updated");

        when(wordService.editWord(1L, updatedWord)).thenReturn(null);

        mockMvc.perform(put("/api/v1/words/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Updated\", \"wordType\": \"ADJECTIVE\"}")
                        .header("Authorization", "Bearer valid_token"))
                .andExpect(status().isNotFound());
    }
}
