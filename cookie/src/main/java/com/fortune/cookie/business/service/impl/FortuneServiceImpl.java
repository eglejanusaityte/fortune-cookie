package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.enums.WordType;
import com.fortune.cookie.business.mappers.FortuneMapper;
import com.fortune.cookie.business.repository.FortuneRepository;
import com.fortune.cookie.business.repository.NeededWordRepository;
import com.fortune.cookie.business.repository.model.FortuneDAO;
import com.fortune.cookie.business.repository.model.NeededWordDAO;
import com.fortune.cookie.business.service.FortuneService;
import com.fortune.cookie.model.Fortune;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FortuneServiceImpl implements FortuneService {

    @Autowired
    private FortuneRepository fortuneRepository;

    @Autowired
    private NeededWordRepository neededWordRepository;

    @Autowired
    private FortuneMapper fortuneMapper;

    @Override
    public Page<Fortune> getAllFortunes(Integer page) {
        Pageable fortunePage = PageRequest.of(page, 10);
        return fortuneRepository.findAll(fortunePage).map(fortuneMapper::fortuneDAOToFortune);
    }

    @Override
    public Fortune getFortuneById(Long id) {
        Optional<FortuneDAO> optionalFortuneDAO = fortuneRepository.findById(id);
        return optionalFortuneDAO.map(fortuneMapper::fortuneDAOToFortune).orElse(null);
    }

    @Override
    public Fortune getRandomFortune() {
        Random random = new Random();
        List<FortuneDAO> allFortunes = fortuneRepository.findAll();
        if (allFortunes.isEmpty()) {
            throw new IllegalStateException("No fortunes available");
        }
        return fortuneMapper.fortuneDAOToFortune(allFortunes.get(random.nextInt(allFortunes.size())));
    }

    @Override
    public Fortune getFortune() {
        List<FortuneDAO> allFortunes = fortuneRepository.findAll();
        Random random = new Random();
        FortuneDAO fortuneDAO = allFortunes.get(random.nextInt(allFortunes.size()));
        return fortuneMapper.fortuneDAOToFortune(fortuneDAO);
    }

    @Override
    public void deleteFortune(Long id) {
        fortuneRepository.deleteById(id);
    }

    @Override
    public Fortune createFortune(String sentence, List<Map<String, String>> neededWords){
        FortuneDAO fortuneDAO = new FortuneDAO(sentence);
        Set<NeededWordDAO> neededWordsDAO = new HashSet<>();

        int placeholderCount = StringUtils.countMatches(sentence, "#");
        if (neededWords.size() != placeholderCount) {
            throw new IllegalArgumentException("Number of provided needed words does not match the placeholders in the sentence.");
        }

        for (Map<String, String> word : neededWords) {
            String descriptor = word.get("descriptor");

            NeededWordDAO neededWordDAO = new NeededWordDAO();
            neededWordDAO.setDescriptor(descriptor);
            String wordType = word.get("wordType");
            neededWordDAO.setWordType(WordType.valueOf(wordType));

            neededWordRepository.save(neededWordDAO);
            neededWordsDAO.add(neededWordDAO);
        }

        fortuneDAO.setNeededWordDAOS(neededWordsDAO);
        fortuneDAO = fortuneRepository.save(fortuneDAO);

        return fortuneMapper.fortuneDAOToFortune(fortuneDAO);
    }

    @Override
    public Fortune updateFortune(Long fortuneId, String sentence, List<Map<String, String>> neededWords) {
        FortuneDAO fortuneDAO = fortuneRepository.findById(fortuneId)
                .orElseThrow(() -> new IllegalArgumentException("Fortune not found with ID: " + fortuneId));
        if(sentence != null){
            fortuneDAO.setSentence(sentence);
        } else {
            sentence = fortuneDAO.getSentence();
        }
        Set<NeededWordDAO> existingNeededWords = fortuneDAO.getNeededWordDAOS();
        for (NeededWordDAO neededWordDAO : existingNeededWords) {
            neededWordRepository.delete(neededWordDAO);
        }
        fortuneDAO.getNeededWordDAOS().clear();

        Set<NeededWordDAO> updatedNeededWordsDAO = new HashSet<>();
        int placeholderCount = StringUtils.countMatches(sentence, "#");
        if (neededWords.size() != placeholderCount) {
            throw new IllegalArgumentException("Number of provided needed words does not match the placeholders in the sentence.");
        }
        for (Map<String, String> word : neededWords) {
            String descriptor = word.get("descriptor");
            NeededWordDAO neededWordDAO = new NeededWordDAO();
            neededWordDAO.setDescriptor(descriptor);
            String wordType = word.get("wordType");
            neededWordDAO.setWordType(WordType.valueOf(wordType));
            neededWordRepository.save(neededWordDAO);
            updatedNeededWordsDAO.add(neededWordDAO);
        }
        fortuneDAO.setNeededWordDAOS(updatedNeededWordsDAO);
        fortuneDAO = fortuneRepository.save(fortuneDAO);
        return fortuneMapper.fortuneDAOToFortune(fortuneDAO);
    }

    private WordType getWordTypeFromPlaceholder(String placeholder) {
        return switch (placeholder.toUpperCase()) {
            case "NOUN" -> WordType.NOUN;
            case "NOUN_PLURAL" -> WordType.NOUN_PLURAL;
            case "VERB" -> WordType.VERB;
            case "ADJECTIVE" -> WordType.ADJECTIVE;
            default -> throw new IllegalArgumentException("Invalid word type: " + placeholder);
        };
    }
}
