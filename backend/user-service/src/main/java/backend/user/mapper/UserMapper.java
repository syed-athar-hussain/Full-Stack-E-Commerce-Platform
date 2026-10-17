package backend.user.mapper;

import backend.shared.dto.UserDto;
import backend.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    UserDto toDto(User user);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto dto);
}