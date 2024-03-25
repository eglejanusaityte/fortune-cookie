package com.fortune.cookie.business.mappers;

import com.fortune.cookie.business.repository.model.FortuneDAO;
import com.fortune.cookie.model.Fortune;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FortuneMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "sentence", target = "sentence")
    @Mapping(source = "neededWords", target = "neededWordDAOS")
    FortuneDAO fortuneToFortuneDAO(Fortune fortune);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "sentence", target = "sentence")
    @Mapping(source = "neededWordDAOS", target = "neededWords")
    Fortune fortuneDAOToFortune(FortuneDAO fortuneDAO);
}