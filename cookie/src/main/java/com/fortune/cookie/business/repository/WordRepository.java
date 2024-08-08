package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.WordDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends PagingAndSortingRepository<WordDAO, Long> {
    Page<WordDAO> findByPersonalFalse(Pageable pageable);

    Optional<WordDAO> findById(Long id);

    WordDAO save(WordDAO wordDAO);

    void deleteById(Long id);

    List<WordDAO> findAll();
}