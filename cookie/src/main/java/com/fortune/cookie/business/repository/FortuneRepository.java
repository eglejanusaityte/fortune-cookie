package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.FortuneDAO;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FortuneRepository extends PagingAndSortingRepository<FortuneDAO, Long> {
    Optional<FortuneDAO> findById(Long id);

    void deleteById(Long id);

    FortuneDAO save(FortuneDAO fortuneDAO);

    List<FortuneDAO> findAll();
}