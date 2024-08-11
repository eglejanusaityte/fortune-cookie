package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.enums.WordType;
import com.fortune.cookie.business.mappers.FortuneMapper;
import com.fortune.cookie.business.repository.FortuneRepository;
import com.fortune.cookie.business.repository.NeededWordRepository;
import com.fortune.cookie.business.repository.model.FortuneDAO;
import com.fortune.cookie.business.repository.model.NeededWordDAO;
import com.fortune.cookie.model.Fortune;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FortuneServiceImplTest {

    @Mock
    private FortuneRepository fortuneRepository;

    @Mock
    private NeededWordRepository neededWordRepository;

    @Mock
    private FortuneMapper fortuneMapper;

    @InjectMocks
    private FortuneServiceImpl fortuneService;

    private FortuneDAO fortuneDAO;
    private Fortune fortune;

    private List<FortuneDAO> fortuneDAOList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        fortuneDAO = new FortuneDAO();
        fortuneDAO.setId(1L);
        fortuneDAO.setSentence("You will have a great day!");

        fortune = new Fortune();
        fortune.setId(1L);
        fortune.setSentence("You will have a great day!");

        fortuneDAOList = Collections.singletonList(fortuneDAO);

        when(fortuneMapper.fortuneDAOToFortune(fortuneDAO)).thenReturn(fortune);
    }

    @Test
    public void getAllFortunes_shouldReturnPagedFortunes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<FortuneDAO> fortuneDAOPage = new PageImpl<>(Collections.singletonList(fortuneDAO), pageable, 1);
        when(fortuneRepository.findAll(pageable)).thenReturn(fortuneDAOPage);

        Page<Fortune> result = fortuneService.getAllFortunes(0);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(fortune.getId(), result.getContent().get(0).getId());
        verify(fortuneRepository, times(1)).findAll(pageable);
        verify(fortuneMapper, times(1)).fortuneDAOToFortune(fortuneDAO);
    }

    @Test
    public void getFortuneById_shouldReturnFortune_whenFound() {
        when(fortuneRepository.findById(1L)).thenReturn(Optional.of(fortuneDAO));

        Fortune result = fortuneService.getFortuneById(1L);

        assertNotNull(result);
        assertEquals(fortune.getId(), result.getId());
        verify(fortuneRepository, times(1)).findById(1L);
        verify(fortuneMapper, times(1)).fortuneDAOToFortune(fortuneDAO);
    }

    @Test
    public void getFortuneById_shouldReturnNull_whenNotFound() {
        when(fortuneRepository.findById(1L)).thenReturn(Optional.empty());

        Fortune result = fortuneService.getFortuneById(1L);

        assertNull(result);
        verify(fortuneRepository, times(1)).findById(1L);
        verify(fortuneMapper, times(0)).fortuneDAOToFortune(any());
    }

    @Test
    public void getRandomFortune_shouldReturnFortune_whenFortunesExist() {
        when(fortuneRepository.findAll()).thenReturn(fortuneDAOList);

        Fortune result = fortuneService.getRandomFortune();

        assertNotNull(result);
        assertEquals(fortune.getId(), result.getId());
        verify(fortuneRepository, times(1)).findAll();
        verify(fortuneMapper, times(1)).fortuneDAOToFortune(fortuneDAO);
    }

    @Test
    public void getRandomFortune_shouldThrowException_whenNoFortunesAvailable() {
        when(fortuneRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(IllegalStateException.class, () -> fortuneService.getRandomFortune());
        verify(fortuneRepository, times(1)).findAll();
        verify(fortuneMapper, times(0)).fortuneDAOToFortune(any());
    }

    @Test
    public void getFortune_shouldReturnFortune_whenFortunesExist() {
        when(fortuneRepository.findAll()).thenReturn(fortuneDAOList);

        Fortune result = fortuneService.getFortune();

        assertNotNull(result);
        assertEquals(fortune.getId(), result.getId());
        verify(fortuneRepository, times(1)).findAll();
        verify(fortuneMapper, times(1)).fortuneDAOToFortune(fortuneDAO);
    }

    @Test
    public void getFortune_shouldThrowException_whenNoFortunesAvailable() {
        when(fortuneRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> fortuneService.getFortune());
        verify(fortuneRepository, times(1)).findAll();
        verify(fortuneMapper, times(0)).fortuneDAOToFortune(any());
    }
    @Test
    public void deleteFortune_shouldCallDeleteById() {
        doNothing().when(fortuneRepository).deleteById(1L);

        fortuneService.deleteFortune(1L);

        verify(fortuneRepository, times(1)).deleteById(1L);
    }

    @Test
    public void createFortune_shouldReturnCreatedFortune_whenValidData() {
        String sentence = "Your #1 fortune";
        List<Map<String, String>> neededWords = Collections.singletonList(
                Map.of("descriptor", "lucky", "wordType", "NOUN")
        );

        when(fortuneRepository.save(any(FortuneDAO.class))).thenReturn(fortuneDAO);
        when(neededWordRepository.save(any(NeededWordDAO.class))).thenReturn(new NeededWordDAO());

        Fortune result = fortuneService.createFortune(sentence, neededWords);

        assertNotNull(result);
        assertEquals(fortune.getId(), result.getId());
        verify(fortuneRepository, times(1)).save(any(FortuneDAO.class));
        verify(neededWordRepository, times(1)).save(any(NeededWordDAO.class));
        verify(fortuneMapper, times(1)).fortuneDAOToFortune(fortuneDAO);
    }

    @Test
    public void createFortune_shouldThrowException_whenNumberOfWordsDoesNotMatchPlaceholders() {
        String sentence = "Your #1 fortune";
        List<Map<String, String>> neededWords = Collections.emptyList();

        assertThrows(IllegalArgumentException.class, () ->
                fortuneService.createFortune(sentence, neededWords)
        );
        verify(fortuneRepository, times(0)).save(any(FortuneDAO.class));
        verify(neededWordRepository, times(0)).save(any(NeededWordDAO.class));
    }

    @Test
    public void createFortune_shouldThrowException_whenInvalidWordType() {
        String sentence = "Your #1 fortune";
        List<Map<String, String>> neededWords = Collections.singletonList(
                Map.of("descriptor", "lucky", "wordType", "INVALID")
        );

        assertThrows(IllegalArgumentException.class, () ->
                fortuneService.createFortune(sentence, neededWords)
        );
        verify(fortuneRepository, times(0)).save(any(FortuneDAO.class));
        verify(neededWordRepository, times(0)).save(any(NeededWordDAO.class));
    }

    @Test
    public void updateFortune_shouldReturnUpdatedFortune_whenValidData() {
        String updatedSentence = "Updated #1 fortune";
        fortuneDAO.setSentence("You will have a great day! #ADJECTIVE");
        Set<NeededWordDAO> neededWordDAOs = new HashSet<>();
        NeededWordDAO neededWordDAO = new NeededWordDAO();
        neededWordDAO.setWordType(WordType.ADJECTIVE);
        neededWordDAO.setDescriptor("Something");
        neededWordDAOs.add(neededWordDAO);
        fortuneDAO.setNeededWordDAOS(neededWordDAOs);
        fortuneDAO.setSentence("You will have a great day! #ADJECTIVE");

        List<Map<String, String>> neededWordsNew = Collections.singletonList(
                Map.of("descriptor", "changed", "wordType", "ADJECTIVE")
        );

        when(fortuneRepository.findById(1L)).thenReturn(Optional.of(fortuneDAO));
        when(fortuneRepository.save(any(FortuneDAO.class))).thenReturn(fortuneDAO);

        Fortune result = fortuneService.updateFortune(1L, updatedSentence, neededWordsNew);

        assertNotNull(result);
        assertEquals(fortune.getId(), result.getId());
        verify(fortuneRepository, times(1)).findById(1L);
        verify(fortuneRepository, times(1)).save(any(FortuneDAO.class));
        verify(neededWordRepository, times(1)).save(any(NeededWordDAO.class));
        verify(fortuneMapper, times(1)).fortuneDAOToFortune(fortuneDAO);
    }

    @Test
    public void updateFortune_shouldThrowException_whenFortuneNotFound() {
        when(fortuneRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                fortuneService.updateFortune(1L, "Updated sentence", Collections.emptyList())
        );
        verify(fortuneRepository, times(1)).findById(1L);
        verify(fortuneRepository, times(0)).save(any(FortuneDAO.class));
        verify(neededWordRepository, times(0)).save(any(NeededWordDAO.class));
    }

    @Test
    public void updateFortune_shouldThrowException_whenInvalidWordType() {
        String sentence = "Your #1 fortune";
        List<Map<String, String>> neededWords = Collections.singletonList(
                Map.of("descriptor", "changed", "wordType", "INVALID")
        );

        when(fortuneRepository.findById(1L)).thenReturn(Optional.of(fortuneDAO));

        assertThrows(IllegalArgumentException.class, () ->
                fortuneService.updateFortune(1L, null, neededWords)
        );
        verify(fortuneRepository, times(1)).findById(1L);
        verify(fortuneRepository, times(0)).save(any(FortuneDAO.class));
        verify(neededWordRepository, times(0)).save(any(NeededWordDAO.class));
    }

}
