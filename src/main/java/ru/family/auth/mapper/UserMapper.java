package ru.family.auth.mapper;

import org.mapstruct.Mapper;
import ru.family.auth.domain.PublicUser;
import ru.family.auth.dto.UserRegistrationDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    PublicUser userFromDto(UserRegistrationDTO dto);
}
