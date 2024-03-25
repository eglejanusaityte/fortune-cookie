package com.fortune.cookie.business.service.impl;

import com.fortune.cookie.business.mappers.NeededWordMapper;
import com.fortune.cookie.business.repository.NeededWordRepository;
import com.fortune.cookie.business.repository.model.NeededWordDAO;
import com.fortune.cookie.business.service.NeededWordService;
import com.fortune.cookie.model.NeededWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NeededWordServiceImpl implements NeededWordService {
    @Autowired
    NeededWordRepository neededWordRepository;
    @Autowired
    NeededWordMapper neededWordMapper;

    @Override
    public NeededWord getNeededWordById(Long id) {
        return neededWordMapper.neededWordDAOToNeededWord(neededWordRepository.findById(id)
                .orElse(null));
    }

    @Override
    public List<NeededWord> getAllNeededWords() {
        return neededWordRepository.findAll()
                .stream()
                .map(neededWordMapper::neededWordDAOToNeededWord)
                .collect(Collectors.toList());
    }

    @Override
    public NeededWord editNeededWord(NeededWord neededWord) {
        NeededWordDAO existingNeededWordDAO = neededWordRepository.findById(neededWord.getId()).orElse(null);
        if (existingNeededWordDAO != null) {
            if (neededWord.getDescriptor() != null) {
                existingNeededWordDAO.setDescriptor(neededWord.getDescriptor());
            }
            if (neededWord.getWordType() != null) {
                existingNeededWordDAO.setWordType(neededWord.getWordType());
            }
            neededWordRepository.save(existingNeededWordDAO);
            return neededWordMapper.neededWordDAOToNeededWord(existingNeededWordDAO);
        }
        return null;
    }

    @Override
    public void deleteNeededWord(Long id) {
        neededWordRepository.deleteById(id);
    }

}
