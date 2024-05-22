package com.fortune.cookie.business.mappers;

import com.fortune.cookie.business.repository.model.FortuneCookieDAO;
import com.fortune.cookie.model.FortuneCookie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserMapper.class, FortuneMapper.class})
public interface FortuneCookieMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "date", target = "date"),
            @Mapping(source = "user", target = "userDAO"),
            @Mapping(source = "fortune", target = "fortuneDAO"),
    })
    FortuneCookieDAO fortuneCookieToFortuneCookieDAO(FortuneCookie fortuneCookie);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "date", target = "date"),
            @Mapping(source = "userDAO", target = "user"),
            @Mapping(source = "fortuneDAO", target = "fortune"),
    })
    FortuneCookie fortuneCookieDAOToFortuneCookie(FortuneCookieDAO fortuneCookieDAO);
}
