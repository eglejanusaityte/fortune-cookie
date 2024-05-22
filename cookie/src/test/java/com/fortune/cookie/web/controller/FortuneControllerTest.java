package com.fortune.cookie.web.controller;

import com.fortune.cookie.business.repository.model.FortuneDAO;
import com.fortune.cookie.business.service.FortuneService;
import com.fortune.cookie.config.TestAuthConfig;
import com.fortune.cookie.model.Fortune;
import com.fortune.cookie.model.NeededWord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FortuneController.class)
@Import({TestAuthConfig.class})
class FortuneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FortuneService fortuneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
    public void testGetAllFortunes() throws Exception {
        JpaSort set;
        NeededWord neededWord = new NeededWord();
        FortuneDAO fortuneDAO1 = new FortuneDAO(1L, "fortune text 1", Set.of());
        FortuneDAO fortuneDAO2 = new FortuneDAO(2L, "fortune text 2", Set.of());
        List<FortuneDAO> fortuneDAOList = Arrays.asList(fortuneDAO1, fortuneDAO2);

        List<Fortune> expectedFortunes = Arrays.asList(
                new Fortune(1L, "fortune text 1", Set.of()),
                new Fortune(2L, "fortune text 2", Set.of())
        );
        when(fortuneService.getAllFortunes()).thenReturn(expectedFortunes);
        mockMvc.perform(get("/api/v1/fortunes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}