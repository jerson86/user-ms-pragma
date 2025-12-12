package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.TokenResponse;
import com.pragma.powerup.domain.model.TokenModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IAuthMapper {
    TokenResponse toResponse(TokenModel tokenModel);
}
