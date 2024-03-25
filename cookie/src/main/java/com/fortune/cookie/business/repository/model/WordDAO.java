package com.fortune.cookie.business.repository.model;

import com.fortune.cookie.business.enums.WordType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "word", uniqueConstraints={@UniqueConstraint(columnNames={"word"})})
public class WordDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "word", unique = true)
    private String word;

    @Enumerated(EnumType.STRING)
    @Column(name = "word_type")
    private WordType wordType;

}