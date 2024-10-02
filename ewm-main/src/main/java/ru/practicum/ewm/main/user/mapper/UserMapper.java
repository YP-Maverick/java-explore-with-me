package ru.practicum.ewm.main.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.main.user.dto.CreateUserDto;
import ru.practicum.ewm.main.user.dto.InitiatorDto;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.model.User;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toModel(CreateUserDto createUserDto);

    UserDto toDto(User user);

    InitiatorDto toInitiator(User user);
}
