package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.WordDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<WordDAO, Long> {
}