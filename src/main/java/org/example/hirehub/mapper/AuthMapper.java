package org.example.hirehub.mapper;

import org.example.hirehub.dto.auth.SignUpRequest;
import org.example.hirehub.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface AuthMapper {
    User toEntity (SignUpRequest request);

}
