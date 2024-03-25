package com.fortune.cookie.business.mappers;

import com.fortune.cookie.business.repository.model.NeededWordDAO;
import com.fortune.cookie.model.NeededWord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NeededWordMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "descriptor", target = "descriptor")
    @Mapping(source = "wordType", target = "wordType")
    NeededWordDAO neededWordToNeededWordDAO(NeededWord neededWord);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "descriptor", target = "descriptor")
    @Mapping(source = "wordType", target = "wordType")
    NeededWord neededWordDAOToNeededWord(NeededWordDAO neededWordDAO);
}
