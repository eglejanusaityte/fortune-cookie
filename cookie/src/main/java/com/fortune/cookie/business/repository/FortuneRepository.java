package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.FortuneDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FortuneRepository extends JpaRepository<FortuneDAO, Long> {
}