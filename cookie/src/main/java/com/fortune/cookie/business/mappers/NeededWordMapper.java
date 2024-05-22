package com.fortune.cookie.business.mappers;

import com.fortune.cookie.business.repository.model.NeededWordDAO;
import com.fortune.cookie.model.NeededWord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {FortuneMapper.class})
public interface NeededWordMapper {
    @Mappings ({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "descriptor", target = "descriptor"),
            @Mapping(source = "wordType", target = "wordType"),
    })
    NeededWordDAO neededWordToNeededWordDAO(NeededWord neededWord);
    @Mappings ({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "descriptor", target = "descriptor"),
            @Mapping(source = "wordType", target = "wordType"),
    })
    NeededWord neededWordDAOToNeededWord(NeededWordDAO neededWordDAO);
}
