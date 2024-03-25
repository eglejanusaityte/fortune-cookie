package com.fortune.cookie.business.repository;

import com.fortune.cookie.business.repository.model.NeededWordDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeededWordRepository extends JpaRepository<NeededWordDAO, Long> {
}