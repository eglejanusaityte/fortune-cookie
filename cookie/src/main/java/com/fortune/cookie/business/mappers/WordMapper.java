package com.fortune.cookie.business.mappers;

import com.fortune.cookie.business.repository.model.WordDAO;
import com.fortune.cookie.model.Word;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WordMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "word", target = "word")
    @Mapping(source = "wordType", target = "wordType")
    WordDAO wordToWordDAO(Word word);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "word", target = "word")
    @Mapping(source = "wordType", target = "wordType")
    Word wordDAOToWord(WordDAO wordDAO);
}