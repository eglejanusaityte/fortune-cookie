package com.fortune.cookie.business.mappers;

import com.fortune.cookie.business.dto.FortuneCookieDTO;
import com.fortune.cookie.business.repository.model.FortuneCookieDAO;
import com.fortune.cookie.business.repository.model.UserDAO;
import com.fortune.cookie.model.FortuneCookie;
import com.fortune.cookie.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Mapping(source = "likes", target = "likes")
    FortuneCookieDTO fortuneCookieToFortuneCookieDTO(FortuneCookie fortuneCookie);


    @Mapping(source = "likes", target = "likes")
    FortuneCookieDTO fortuneCookieDAOToFortuneCookieDTO(FortuneCookieDAO fortuneCookieDAO);

    default Set<String> map(Set<UserDAO> userDAOs) {
        return userDAOs.stream()
                .map(UserDAO::getUsername)
                .collect(Collectors.toSet());
    }

    default Set<String> map(List<User> users) {
        return users.stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
    }
}
