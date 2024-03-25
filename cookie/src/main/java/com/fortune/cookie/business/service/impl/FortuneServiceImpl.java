package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.enums.WordType;
import com.fortune.cookie.business.mappers.FortuneMapper;
import com.fortune.cookie.business.repository.FortuneRepository;
import com.fortune.cookie.business.repository.NeededWordRepository;
import com.fortune.cookie.business.repository.model.FortuneDAO;
import com.fortune.cookie.business.repository.model.NeededWordDAO;
import com.fortune.cookie.business.service.FortuneService;
import com.fortune.cookie.model.Fortune;
import com.fortune.cookie.model.NeededWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public List<Fortune> getAllFortunes() {
        List<FortuneDAO> fortuneDAOList = fortuneRepository.findAll();
        return fortuneDAOList.stream().map(fortuneMapper::fortuneDAOToFortune).collect(Collectors.toList());
    }

    @Override
    public Fortune getFortuneById(Long id) {
        Optional<FortuneDAO> optionalFortuneDAO = fortuneRepository.findById(id);
        return optionalFortuneDAO.map(fortuneMapper::fortuneDAOToFortune).orElse(null);
    }

    @Override
    public void deleteFortune(Long id) {
        fortuneRepository.deleteById(id);
    }

    @Override
    public Fortune createFortune(String sentence, Set<String> descriptors) {
        FortuneDAO fortuneDAO = new FortuneDAO(sentence);

        Set<NeededWordDAO> neededWordsDAO = new HashSet<>();

        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(sentence);

        while (matcher.find()) {
            String placeholder = matcher.group(1);

            NeededWordDAO neededWordDAO = new NeededWordDAO();
            neededWordDAO.setWordType(getWordTypeFromPlaceholder(placeholder));

            String descriptor = descriptors.isEmpty() ? "default" : descriptors.iterator().next();
            neededWordDAO.setDescriptor(descriptor);
            descriptors.remove(descriptor);

            neededWordRepository.save(neededWordDAO);
            neededWordsDAO.add(neededWordDAO);
        }

        if (!descriptors.isEmpty()) {
            throw new IllegalArgumentException("Too many descriptors provided.");
        }

        fortuneDAO.setNeededWordDAOS(neededWordsDAO);
        fortuneDAO = fortuneRepository.save(fortuneDAO);

        return fortuneMapper.fortuneDAOToFortune(fortuneDAO);
    }

    private WordType getWordTypeFromPlaceholder(String placeholder) {
        return switch (placeholder.toUpperCase()) {
            case "NOUN" -> WordType.NOUN;
            case "VERB" -> WordType.VERB;
            case "ADJECTIVE" -> WordType.ADJECTIVE;
            default -> throw new IllegalArgumentException("Invalid word type: " + placeholder);
        };
    }
}
